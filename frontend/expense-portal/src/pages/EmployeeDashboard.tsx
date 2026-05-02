import { useEffect, useState } from "react";
import { api, EXPENSE_BASE } from "../api/api";
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

export default function EmployeeDashboard() {
  const u = getUser()!; // logged in user
  const [amount, setAmount] = useState<number | "">("");
  const [description, setDescription] = useState("");
  const [items, setItems] = useState<Expense[]>([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  async function load() {
    setErr(null);
    try {
      const { data } = await api.get<Expense[]>(
        `${EXPENSE_BASE}?employeeId=${u.employeeId}`
      );
      setItems(data);
    } catch (e: any) {
      const msg = e?.response?.data ?? e.message;
      setErr(msg);
      toast.error(`${msg}`);
    }
  }

  useEffect(() => {
    load();
  }, []);

  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => setMessage(null), 4000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  useEffect(() => {
    if (err) {
      const timer = setTimeout(() => setErr(null), 4000);
      return () => clearTimeout(timer);
    }
  }, [err]);

  async function submitClaim(e: React.FormEvent) {
    e.preventDefault();

    if (!amount || amount <= 0) {
      toast.warning("Amount is required and must be greater than 0");
      return;
    }
    if (!description.trim()) {
      toast.warning("Description is required");
      return;
    }

    setLoading(true);
    setErr(null);
    try {
      const payload = {
        amount: Number(amount),
        description: description.trim(),
        employeeId: u.employeeId,
        managerId: u.managerId,
      };
      await api.post(EXPENSE_BASE, payload);
      setAmount("");
      setDescription("");
      await load();
      setMessage("Claim submitted successfully.");
      toast.success("Claim submitted successfully.");
    } catch (e: any) {
      const msg = e?.response?.data ?? e.message;
      setErr(msg);
      toast.error(`${msg}`);
    } finally {
      setLoading(false);
    }
  }

  async function deleteClaim(expense: Expense) {
    if (expense.status !== "PENDING") {
      const m = "Only pending claims can be deleted.";
      setMessage(m);
      toast.info(m);
      return;
    }

    try {
      await api.delete(`${EXPENSE_BASE}/${expense.id}?actorId=${u.employeeId}`);
      await load();
      setMessage("Claim deleted successfully.");
      toast.success("Claim deleted successfully.");
    } catch (e: any) {
      const msg = e?.response?.data ?? e.message;
      setErr(msg);
      toast.error(`${msg}`);
    }
  }

  return (
    <div className="row">
      <div className="col">
        <div className="card">
          {err && <div className="error">{err}</div>}
          {message && <div className="info">{message}</div>}

          <table className="table" style={{ marginTop: 12 }}>
            <thead>
              <tr>
                <th>Amount</th>
                <th>Description</th>
                <th>Status</th>
                <th>Created</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {items.map((x) => (
                <tr key={x.id}>
                  <td>{x.amount.toFixed(2)}</td>
                  <td>{x.description}</td>
                  <td>
                    <span className={`badge ${x.status}`}>{x.status}</span>
                  </td>
                  <td className="small">
                    {new Date(x.createdAt).toLocaleString()}
                  </td>
                  <td>
                    <button
                      className="btn ghost"
                      disabled={x.status !== "PENDING"}
                      onClick={() => deleteClaim(x)}
                    >
                      {x.status === "PENDING" ? "Delete" : "Locked"}
                    </button>
                  </td>
                </tr>
              ))}
              {items.length === 0 && (
                <tr>
                  <td colSpan={5} className="small">
                    No claims yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      <div className="col" style={{ maxWidth: 420 }}>
        <div className="card">
          <h2>New Claim</h2>
          <form onSubmit={submitClaim} className="row">
            <div className="col">
              <label className="small">Amount</label>
              <input
                className="input"
                type="number"
                step="0.01"
                value={amount}
                onChange={(e) =>
                  setAmount(e.target.value ? Number(e.target.value) : "")
                }
              />
            </div>
            <div className="col">
              <label className="small">Description</label>
              <input
                className="input"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
            </div>
            <button className="btn" disabled={loading} aria-busy={loading}>
              {loading ? <span className="loader" /> : "Submit Claim"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
