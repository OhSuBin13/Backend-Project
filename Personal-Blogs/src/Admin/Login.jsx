import { useState } from "react";
import { useNavigate } from "react-router-dom";
export default function Login() {
  const [name, setname] = useState();
  const [password, setpassword] = useState();
  const [error, seterror] = useState();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    console.log(e.target.name.value);

    const response = await fetch("http://localhost:3000/api/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: e.target.name.value,
        password: e.target.password.value,
      }),
    });
    if (response.status == 404) {
      seterror("Invalid credentials");
      setname("");
      setpassword("");
    } else if (response.status == 200) {
      console.log(await response.json());
      navigate("/Admin/Dashboard");
    }
  }
  return (
    <div className="flex flex-col justify-center items-center">
      <h1 className="underline">Personal Blogs</h1>

      <form
        action="POST"
        className="min-w-60 min-h-60   bg-gray-400 flex flex-col justify-center items-center p-24 m-5 rounded-xl "
        onSubmit={handleSubmit}
      >
        <h1 className="relative bottom-4 filter drop-shadow-[0_0_10px_white]">
          Login
        </h1>
        <input
          type="text"
          name="name"
          className="mt-3bg-gray-100 border-2 border-gray-300 rounded-lg p-1 focus:border-gray-500 focus:outline-none transition-colors duration-300"
          value={name}
          onChange={(e) => {
            setname(e.target.value);
            seterror(false);
          }}
          placeholder="Name"
        />

        <input
          type="password"
          name="password"
          className="mt-3 bg-gray-100 border-2 border-gray-300 rounded-lg p-1 focus:border-gray-500 focus:outline-none transition-colors duration-300"
          value={password}
          onChange={(e) => {
            setpassword(e.target.value);
          }}
          placeholder="Password"
        />
        <input
          type="submit"
          className="bg-slate-100 mt-3 hover:bg-slate-600 rounded-xl p-2 cursor-pointer"
          value="submit"
        />
        {error && name == "" && <p className="text-red-500">{error}</p>}
      </form>
    </div>
  );
}
