import { type FormEvent, useState } from "react";
import { api, AUTH_BASE } from "../api/api";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export default function Register() {
  const [role, setRole] = useState<"EMPLOYEE" | "MANAGER">("EMPLOYEE");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [employeeId, setEmployeeId] = useState<number | "">("");
  const [managerId, setManagerId] = useState<number | "">("");
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  async function submit(e: FormEvent) {
    e.preventDefault();

    // Combined "all empty" check
    if (!username.trim() && !password.trim() && !employeeId && !managerId) {
      toast.error("All credentials are required");
      return;
    }

    // Per-field checks
    if (!username.trim()) {
      toast.warning("Username is required");
      return;
    }
    if (!password.trim()) {
      toast.warning("Password is required");
      return;
    }

    if (role === "EMPLOYEE" && !employeeId) {
      toast.warning("Employee ID is required for Employee registration");
      return;
    }

    if (role === "MANAGER" && !managerId) {
      toast.warning("Manager ID is required for Manager registration");
      return;
    }

    // Build payload
    const payload: any = { username, password, role };
    if (role === "EMPLOYEE") {
      payload.employeeId = Number(employeeId);
      if (managerId) payload.managerId = Number(managerId);
    } else {
      payload.managerId = Number(managerId);
      payload.employeeId = Number(managerId); // same as managerId
    }

    setLoading(true);
    try {
      await api.post(`${AUTH_BASE}/register`, payload);
      toast.success("Registered successfully. You can sign in now.");
      nav("/login");
    } catch (e: any) {
      const backendMsg =
        e?.response?.data?.message ??
        e?.response?.data ??
        e.message ??
        "Registration failed";

      let msg: string;

      // If backend provides an array of conflicts
      const conflicts: string[] = e?.response?.data?.conflicts ?? [];
      if (conflicts.length > 0) {
        // Map each conflict to a friendly message
        const conflictMessages = conflicts.map((c) => {
          switch (c) {
            case "USERNAME":
              return "Username is already taken";
            case "EMPLOYEE_ID":
              return "Employee ID is already registered";
            case "MANAGER_ID":
              return "Manager ID is already registered";
            default:
              return c;
          }
        });
        // Join multiple messages
        msg = conflictMessages.join(" and ") + ".";
      } else {
        // If backend still sends single structured code
        const code = e?.response?.data?.code;
        if (code) {
          switch (code) {
            case "USERNAME_EXISTS":
              msg = "This username is already taken.";
              break;
            case "EMPLOYEE_ID_EXISTS":
              msg = "This Employee ID is already registered.";
              break;
            case "MANAGER_ID_EXISTS":
              msg = "This Manager ID is already registered.";
              break;
            default:
              msg = backendMsg;
          }
        } else if (typeof backendMsg === "string") {
          // Fallback keyword matching if backend sends raw SQL text
          const lower = backendMsg.toLowerCase();
          if (lower.includes("username") && lower.includes("employee")) {
            msg = "Username and Employee ID are already registered.";
          } else if (lower.includes("username") && lower.includes("manager")) {
            msg = "Username and Manager ID are already registered.";
          } else if (lower.includes("employee") && lower.includes("manager")) {
            msg = "Employee ID and Manager ID are already registered.";
          } else if (lower.includes("username")) {
            msg = "This username is already taken.";
          } else if (lower.includes("employee")) {
            msg = "This Employee ID is already registered.";
          } else if (lower.includes("manager")) {
            msg = "This Manager ID is already registered or invalid.";
          } else {
            msg = backendMsg;
          }
        } else {
          msg = "Registration failed. Please try again.";
        }
      }

      toast.error(`${msg}`);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card" style={{ maxWidth: 520, margin: "40px auto" }}>
      <h1>Create account</h1>
      <form onSubmit={submit} className="row">
        <div className="col">
          <label className="small">Role</label>
          <select
            className="input"
            value={role}
            onChange={(e) => setRole(e.target.value as any)}
          >
            <option value="EMPLOYEE">Employee</option>
            <option value="MANAGER">Manager</option>
          </select>
        </div>

        <div className="col">
          <label className="small">Username</label>
          <input
            className="input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="username"
            autoComplete="username"
          />
        </div>

        <div className="col">
          <label className="small">Password</label>
          <input
            className="input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
            autoComplete="new-password"
          />
        </div>

        {role === "EMPLOYEE" && (
          <div className="col">
            <label className="small">Employee ID</label>
            <input
              className="input"
              type="number"
              value={employeeId}
              onChange={(e) => setEmployeeId(e.target.value as any)}
              placeholder="e.g. 101"
            />
          </div>
        )}

        <div className="col">
          <label className="small">
            {role === "MANAGER" ? "Manager ID" : "Manager ID (optional)"}
          </label>
          <input
            className="input"
            type="number"
            value={managerId}
            onChange={(e) => setManagerId(e.target.value as any)}
            placeholder={role === "MANAGER" ? "e.g. 201" : "e.g. your manager’s ID"}
          />
        </div>

        <div style={{ display: "flex", gap: 8 }}>
          <button className="btn" disabled={loading} aria-busy={loading}>
            {loading ? <span className="loader" /> : "Register"}
          </button>
          <Link to="/login" className="btn ghost">
            Back to login
          </Link>
        </div>
      </form>
    </div>
  );
}
