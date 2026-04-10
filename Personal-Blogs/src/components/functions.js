export async function fetchData(url, method) {
  const response = await fetch(url, { method });

  if (!response.ok) {
    const contentType = response.headers.get("Content-Type");
    if (contentType && contentType.includes("text/html")) {
      const errorText = await response.text();
      throw new Error(`HTML response received: ${errorText}`);
    }
    throw new Error(`Network response was not ok. status: ${response.status}`);
  }

  try {
    const data = await response.json();
    return data;
  } catch (error) {
    throw new Error("Failed to parse JSON response");
  }
}
