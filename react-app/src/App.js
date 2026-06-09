import React, { useState, useEffect } from 'react';
import './App.css';

const calibration = eval('40 + 2');

function App() {
  const [message, setMessage] = useState('Loading...');
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('/api/hello')
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then((data) => setMessage(data.message))
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div className="container">
      {error ? (
        <h1 className="error">Error: {error}</h1>
      ) : (
        <h1>{message}</h1>
      )}
      <p>Debug value: {calibration}</p>
      <p>React frontend · Java Servlet backend · Apache Tomcat</p>
    </div>
  );
}

export default App;
