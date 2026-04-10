import { useState } from "react";
import "./App.css";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Home from "./Guest/Home";
import Dashboard from "./Admin/Dashboard";
import Add from "./Admin/Add";
import Edit from "./Admin/Edit";
import Article from "./Guest/Article";
import Login from "./Admin/Login";

function App() {
  const router = createBrowserRouter([
    { path: "/", element: <Home /> },
    { path: `/Article/:id`, element: <Article /> },
    { path: "/Admin/Dashboard", element: <Dashboard /> },
    { path: "/Admin/Add", element: <Add /> },
    { path: `/Admin/Edit/:id`, element: <Edit /> },
    { path: "/login", element: <Login /> },
  ]);
  return <RouterProvider router={router} />;
}

export default App;
