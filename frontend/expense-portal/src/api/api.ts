import axios from "axios";
import { getToken } from "../auth/auth";

export const AUTH_BASE = import.meta.env.VITE_AUTH_BASE as string;
export const EMPLOYEE_BASE = import.meta.env.VITE_EMPLOYEE_BASE as string;
export const EXPENSE_BASE = import.meta.env.VITE_EXPENSE_BASE as string;

export const api = axios.create();

api.interceptors.request.use((config) => {
  const t = getToken();
  if (t) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${t}`;
  }
  return config;
});
