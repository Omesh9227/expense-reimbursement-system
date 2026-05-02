import { type FormEvent, useState } from "react";
import { api, AUTH_BASE } from "../api/api";
import { setToken, getUser } from "../auth/auth";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  async function submit(e: FormEvent) {
    e.preventDefault();

    if (!username.trim() && !password.trim()) {
      toast.error("All credentials are required");
      return;
    }
    if (!username.trim()) {
      toast.warning("Username is required");
      return;
    }
    if (!password.trim()) {
      toast.warning("Password is required");
      return;
    }

    setLoading(true);
    try {
      const { data } = await api.post(`${AUTH_BASE}/login`, { username, password });
      if (!data?.token) throw new Error("No token in response");
      setToken(data.token);
      toast.success("Signed in successfully");
      const u = getUser();
      nav(u?.role === "MANAGER" ? "/manager" : "/employee", { replace: true });
    } catch (e: any) {
      const msg = e?.response?.data ?? e.message ?? "Login failed";
      toast.error(`${msg}`);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card" style={{ maxWidth: 420, margin: "40px auto" }}>
      <h1>Sign in</h1>
      <form onSubmit={submit} className="row">
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
            autoComplete="current-password"
          />
        </div>

        <div style={{ display: "flex", gap: 8 }}>
          <button className="btn" disabled={loading} aria-busy={loading}>
            {loading ? <span className="loader" /> : "Sign in"}
          </button>
          <Link to="/register" className="btn ghost">
            Create account
          </Link>
        </div>
      </form>
    </div>
  );
}
