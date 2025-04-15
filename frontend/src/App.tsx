import React from 'react';
import logo from './logo.svg';
import './App.css'; import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import CadastroCoordenadorPage from './pages/coordenador/CadastroCoordenadorPage';
import CoordenadorPage from './pages/coordenador/CoordenadorPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/coordenadores/cadastro" element={<CadastroCoordenadorPage />} />
        <Route path="/coordenadores" element={<CoordenadorPage />} /> </Routes>
    </Router>
  );
}

export default App;
