import { useEffect, useState } from "react";

function Clock() {
    const [time, setTime] = useState(0);
  
    useEffect( () => {setInterval(handleClick, 1000)}, [])
  
    function handleClick() { setTime(time => time+1) }
    return <div><p>{time}</p></div>
}

export default Clock