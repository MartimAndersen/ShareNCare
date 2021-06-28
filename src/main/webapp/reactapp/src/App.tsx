import { ChangeEvent } from 'react';
import { useEffect, useState } from 'react';
import { isPropertySignature } from 'typescript';
import './App.css';

function Clock() {
  const [time, setTime] = useState(0);

  useEffect( () => {setInterval(handleClick, 1000)}, [])

  function handleClick() { setTime(time => time+1) }
  return <div><p>{time}</p></div>
}

const europe_countries = ["Portugal", "Espanha", "França", "Alemanha"]

interface Country {
  todayCases: number,
  population: number,
  country: String,
  countryInfo: {flag:string}
}

function CountryList(props:{filter:string}) {
  const [countries, setCountries] = useState([] as Country[])

  function loadCountries() {
    fetch("https://corona.lmao.ninja/v3/covid-19/countries")
    .then( data => data.json() )
    .then( list => { setCountries(list); } )
  }
  useEffect( loadCountries, [] )

  return<CountryListView countries={countries} filter={props.filter}/>
}

function CountryListView(props:{countries:Country[], filter:string}) {
  return <ul>
    { props
    .countries
    .filter( c => c.country.includes(props.filter))
    .map( c => <li><img src={c.countryInfo.flag} width="50px"/>{c.country}: {c.todayCases/c.population*1e5}</li>) }
  </ul>
}

function App() {

  const[filter, setFilter] = useState("")

  function handleChange(e:ChangeEvent<HTMLInputElement>) {
    setFilter(e.target.value)
  }

  return (
    <div>
      <Clock/>
      <input type={"text"} value={filter} onChange={handleChange}/>
      <CountryList filter={filter}/>
    </div>
  );
}

export default App;