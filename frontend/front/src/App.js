import { useEffect, useState } from 'react';
import './App.css';
import Form from './Form';
import Table from './Table';

function App() {
  //Objetos
  const planta = {
    name : '',
    species : '',
    location : '', 
  }

  // UseState
  const [btnCadastrar, setBtnCadastrar] = useState(true);
  const [plantas, setPlantas] = useState([]);
  const [objPlanta, setObjPlanta] = useState(planta);

  // UseEffect
  useEffect(() => {
     fetch("http://localhost:8080/plants")
     .then(retorno => retorno.json())
     .then(retorno_conveertido => setPlantas(retorno_conveertido))
  }, []);

  //obtendo dados do formulário
  const aoDigitar = (e) =>  {
    setObjPlanta({...objPlanta, [e.target.name]:e.target.value})
  }

  //cadastrar as plantas
  //estou usando o id de um usuario de teste, trocar isso
  const cadastrar = () => {
    fetch('http://localhost:8080/plants/user/7', {
      method:'post',
      body:JSON.stringify(objPlanta),
      headers:{
        'Content-type':'application/json',
        'Accept':'application/json'
      }
    })
    .then(retorno => retorno.json())
    .then(retorno_conveertido => {
      if(retorno_conveertido.message !== undefined) {
        alert(retorno_conveertido.message);
      }else {
        setPlantas([...plantas, retorno_conveertido]);
        alert('Planta Cadastrada')
      }
    })
  }

  return (
    <div>
      <Form botao={btnCadastrar} eventoTeclado={aoDigitar} cadastrar={cadastrar}/>
      <Table vetor={plantas}/>
    </div>
  );
}

export default App;
