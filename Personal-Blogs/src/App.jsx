import { useState } from "react";
import "./App.css";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Add from "./Admin/Add";
import Home from "./Guest/Home";
import Article from "./Guest/Article";
import Dashboard from "./Admin/Dashboard";

function App() {
  const router = createBrowserRouter([
    { path: "/Admin/Add", element: <Add /> },
    { path: "/Admin/Dashboard", element: <Dashboard /> },
    { path: "/", element: <Home /> },
    { path: `/Article/:id`, element: <Article /> },
  ]);
  return <RouterProvider router={router} />;
}

export default App;
