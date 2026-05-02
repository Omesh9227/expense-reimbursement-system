import { Navigate } from "react-router-dom";
import { getUser, useAuthListener } from "../auth/auth";
import type { JSX } from "react";

export default function ProtectedRoute({
  children,
  roles,
}: {
  children: JSX.Element;
  roles?: string[];
}) {
  useAuthListener();
  const user = getUser();

  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/login" replace />;

  return children;
}
