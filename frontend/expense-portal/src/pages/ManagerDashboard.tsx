import { useEffect, useState, type JSX } from "react";
import { api, EMPLOYEE_BASE, EXPENSE_BASE } from "../api/api";
import { getUser } from "../auth/auth";
import { toast } from "react-toastify";

type Expense = {
  id: number;
  employeeId: number;
  managerId: number;
  amount: number;
  description: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
  createdAt: string;
};

type Employee = {
  id: number;
  username: string;
  role: "EMPLOYEE" | "MANAGER";
  employeeId: number;
  managerId?: number;
};

export default function ManagerDashboard() {
  const [tab, setTab] = useState<"APPROVALS" | "HISTORY" | "EMPLOYEES">("APPROVALS");
  const [items, setItems] = useState<Expense[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [msg, setMsg] = useState("");
  const u = getUser();

  useEffect(() => {
    if (!u) return;
    if (tab === "APPROVALS") loadApprovals();
    else if (tab === "HISTORY") loadHistory();
    else if (tab === "EMPLOYEES") loadEmployees();
  }, [tab]);

  async function loadApprovals() {
    try {
      const { data } = await api.get(`${EXPENSE_BASE}?managerId=${u!.employeeId}`);
      setItems(data.filter((exp: Expense) => exp.status === "PENDING"));
    } catch (e: any) {
      handleError("Failed to load approvals", e);
    }
  }

  async function loadHistory() {
    try {
      const { data } = await api.get(`${EXPENSE_BASE}?managerId=${u!.employeeId}`);
      setItems(data.filter((x: Expense) => x.status !== "PENDING"));
    } catch (e: any) {
      handleError("Failed to load history", e);
    }
  }

  async function loadEmployees() {
    try {
      const { data } = await api.get(`${EMPLOYEE_BASE}?managerId=${u!.employeeId}`);
      setEmployees(data);
    } catch (e: any) {
      handleError("Failed to load employees", e);
    }
  }

  async function approveClaim(id: number) {
    try {
      await api.put(`${EXPENSE_BASE}/${id}/approve`);
      toast.success("Claim approved");
      loadApprovals();
    } catch (e: any) {
      handleError("Approve failed", e);
    }
  }

  async function rejectClaim(id: number) {
    try {
      await api.put(`${EXPENSE_BASE}/${id}/reject`);
      toast.success("Claim rejected");
      loadApprovals();
    } catch (e: any) {
      handleError("Reject failed", e);
    }
  }

  async function deleteEmployee(id: number) {
    if (!window.confirm("Are you sure you want to delete this employee?")) return;
    try {
      await api.delete(`${EMPLOYEE_BASE}/${id}`);
      toast.success("Employee deleted");
      loadEmployees();
    } catch (e: any) {
      handleError("Delete failed", e);
    }
  }

  function handleError(prefix: string, e: any) {
    const m = `${prefix}: ${e.response?.data?.message || e.message}`;
    setMsg(m);
    toast.error(m);
  }

  return (
    <div className="container">
      <h1>Manager Dashboard</h1>

      <div className="nav mb-4">
        <a className={tab === "APPROVALS" ? "active" : ""} onClick={() => setTab("APPROVALS")}>
          Approvals
        </a>
        <a className={tab === "HISTORY" ? "active" : ""} onClick={() => setTab("HISTORY")}>
          History
        </a>
        <a className={tab === "EMPLOYEES" ? "active" : ""} onClick={() => setTab("EMPLOYEES")}>
          Employees
        </a>
      </div>

      {msg && <div className="mb-4 small">{msg}</div>}

      {tab === "APPROVALS" && (
        <ExpenseTable
          items={items}
          actions={(id) => (
            <>
              <button className="btn mr-2" onClick={() => approveClaim(id)}>Approve</button>
              <button className="btn secondary" onClick={() => rejectClaim(id)}>Reject</button>
            </>
          )}
        />
      )}

      {tab === "HISTORY" && (
        <ExpenseTable items={items} showStatus />
      )}

      {tab === "EMPLOYEES" && (
        <EmployeeTable employees={employees} onDelete={deleteEmployee} />
      )}
    </div>
  );
}

function ExpenseTable({ items, actions, showStatus = false }:
  { items: Expense[], actions?: (id: number) => JSX.Element, showStatus?: boolean }) {
  return (
    <table className="table table-striped">
      <thead>
        <tr>
          <th>Employee ID</th>
          <th>Description</th>
          <th>Amount (₹)</th>
          {showStatus && <th>Status</th>}
          <th>Created</th>
          {actions && <th>Actions</th>}
        </tr>
      </thead>
      <tbody>
        {items.map((x) => (
          <tr key={x.id}>
            <td>{x.employeeId}</td>
            <td>{x.description}</td>
            <td>₹{x.amount.toFixed(2)}</td>
            {showStatus && (
              <td><span className={`badge ${x.status}`}>{x.status}</span></td>
            )}
            <td className="small">{new Date(x.createdAt).toLocaleString()}</td>
            {actions && <td>{actions(x.id)}</td>}
          </tr>
        ))}
        {items.length === 0 && (
          <tr><td colSpan={showStatus ? 5 : 4} className="small text-center">No records found.</td></tr>
        )}
      </tbody>
    </table>
  );
}

function EmployeeTable({ employees, onDelete }: { employees: Employee[], onDelete: (id: number) => void }) {
  return (
    <table className="table table-striped">
      <thead>
        <tr>
          <th>Employee ID</th>
          <th>Username</th>
          <th>Role</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {employees.map((emp) => (
          <tr key={emp.id}>
            <td>{emp.employeeId}</td>
            <td>{emp.username}</td>
            <td>
              <span className={`badge ${emp.role}`}>{emp.role}</span>
            </td>
            <td>
              {emp.role !== "MANAGER" && (
                <button className="btn danger" onClick={() => onDelete(emp.id)}>Delete</button>
              )}
            </td>
          </tr>
        ))}
        {employees.length === 0 && (
          <tr><td colSpan={4} className="small text-center">No employees found under you.</td></tr>
        )}
      </tbody>
    </table>
  );
}
