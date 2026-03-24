import { useNavigate } from "react-router";
import { useEffect } from "react";
import type { ViewConfig } from "@vaadin/hilla-file-router/types.js";

export const config: ViewConfig = {
  title: "re:solve",
  flowLayout: false,
};

export default function IndexView() {
  const navigate = useNavigate();

  useEffect(() => {
    navigate("/tickets", { replace: true });
  }, [navigate]);

  return null;
}
