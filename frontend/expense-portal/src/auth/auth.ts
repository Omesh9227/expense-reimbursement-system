import { jwtDecode } from "jwt-decode";
import React from "react";

const TOKEN_KEY = "auth_token";

type Decoded = {
  sub: string;             
  email: string;
  role: "EMPLOYEE" | "MANAGER";
  employeeId?: number;
  managerId?: number;
  exp?: number;
};

let listeners: Array<() => void> = [];

export function setToken(token: string | null) {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
  listeners.forEach(fn => fn());
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function getUser(): (Decoded & { token: string }) | null {
  const t = getToken();
  if (!t) return null;
  try {
    const d = jwtDecode<Decoded>(t);
    if (d.exp && d.exp * 1000 < Date.now()) { 
      setToken(null); 
      return null; 
    }
    return { ...d, token: t };
  } catch { 
    return null; 
  }
}

export function useAuthListener() {
  React.useEffect(() => {
    const fn = () => {};
    listeners.push(fn);
    return () => { listeners = listeners.filter(f => f !== fn); };
  }, []);
}

export function logout() {
  setToken(null);
  window.location.href = "/login";
}
