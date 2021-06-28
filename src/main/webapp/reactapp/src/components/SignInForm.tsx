import { ChangeEvent, useState } from "react"

function SignInForm() {

    const [open, setOpen] = useState(false)
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    function handleChangeUsername(e:ChangeEvent<HTMLInputElement>) {
        setUsername(e.target.value)
    }
    
    function handleChangePassword(e:ChangeEvent<HTMLInputElement>) {
        setPassword(e.target.value)
    }
    function register() {
        //call the server
        fetch("/register",
            {method:"POST",
            headers:{accept: "application/json"},
            body: JSON.stringify({user:username, pass:password})
            } )
        //.then(data => if(data.ok) { setSignedIn(true)})
        setOpen(false)
    }
    function cancel() {
        setOpen(false)
    }
    function openForm() {
        setOpen(true)
    }

    return <div>
        {!open && <button onClick={openForm}>Sign in here</button>}
        {open && <form onSubmit={(e: any) => { e.preventDefault() }}>
            <div>Username: <input type={"text"} value={username} onChange={handleChangeUsername}/></div>
            <div>Password: <input type={"text"} value={password} onChange={handleChangePassword}/></div>
            <button onClick={register}>Sign In</button>
            <button onClick={cancel}>Cancel</button>
        </form>}
    </div>
}

export default SignInForm