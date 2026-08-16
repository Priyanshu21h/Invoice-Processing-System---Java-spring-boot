import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";

function Navbar() {

    const navigate = useNavigate();

    const { logout } = useContext(AuthContext);

    const handleLogout = () => {

        logout();

        navigate("/login");

    };

    return (

        <nav className="navbar">

            <h2>Invoice Management System</h2>

            <button onClick={handleLogout}>

                Logout

            </button>

        </nav>

    );

}

export default Navbar;