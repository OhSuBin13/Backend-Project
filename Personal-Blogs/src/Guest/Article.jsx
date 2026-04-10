import { useState, useEffect } from "react";
import { fetchData } from "../components/functions";
import { useParams } from "react-router-dom";
export default function Article() {
  const { id } = useParams();
  const [article, setArticle] = useState();
  useEffect(() => {
    async function fetchdata() {
      const result = await fetch(`http:localhost:3000/api/articles/${id}`);
      const res = await result.json();
      setArticle(res);
    }
    fetchdata();
  }, []);

  if (!article) {
    return <h2>Loading...</h2>;
  }
  return (
    <div>
      <h1 className="px-2 text-center">Article</h1>
      <div className="border-2 border-gray-200 p-2 m-2 rounded-xl">
        <h2>{article.title}</h2>
        <h3 className="text-black text-left text-center">
          {article.creationtime}
        </h3>
        <p className="text-gray-800 text-center text-lg my-3">
          {article.content}
        </p>
      </div>
    </div>
  );
}
