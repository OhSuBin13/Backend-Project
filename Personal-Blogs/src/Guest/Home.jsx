import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";

export default function Home() {
  const [articles, setArticles] = useState([]);
  const navigate = useNavigate();
  useEffect(() => {
    const fetchArticles = async () => {
      try {
        const response = await fetch("http://localhost:3000/api/articles", {
          method: "GET",
        });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        console.log(response);
        setArticles(data);
      } catch (error) {
        console.error("Error fetching articles:", error);
      }
    };

    fetchArticles();
  }, []);

  return (
    <div>
      <div className="flex flex-row justify-between items-center">
        <h1 className="text-left px-2">Personal Blog</h1>
        <Link to="/login">
          <h3 className="text-right px-2 bg-slate-200 rounded-2xl cursor-pointer">
            Login as Admin
          </h3>
        </Link>
      </div>
      {articles.length > 0 ? (
        articles.map((article, index) => (
          <div
            key={index}
            className="border-2 border-gray-200 p-2 m-2 flex justify-between items-center cursor-pointer"
            onClick={() => {
              navigate(`/articles/${article.id}`);
            }}
          >
            <h2 className="text-black text-left">{article.title}</h2>
            <h2 className="text-gray-300 text-right">{article.creationtime}</h2>
          </div>
        ))
      ) : (
        <p>No articles available</p>
      )}
    </div>
  );
}
