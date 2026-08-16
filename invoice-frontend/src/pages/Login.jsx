import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { loginUser } from "../api/authApi";
import { AuthContext } from "../context/AuthContext";

function Login() {

    const navigate = useNavigate();

    const { login } = useContext(AuthContext);

    const [formData, setFormData] = useState({
        username: "",
        password: ""
    });

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const response = await loginUser(formData);

            login(response.data.token);

            toast.success("Login successful.");

            navigate("/dashboard");

        } catch (error) {

            console.log(error);

            console.log(error.response);

            toast.error(
                error.response?.data?.message || "Login failed."
            );

        }

    };

    return (

        <div className="login-container">

            <div className="login-card">

                <h1>Invoice Management System</h1>

                <form onSubmit={handleSubmit}>

                    <input
                        type="text"
                        name="username"
                        placeholder="Username"
                        value={formData.username}
                        onChange={handleChange}
                    />

                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                    />

                    <button type="submit">
                        Login
                    </button>

                </form>

            </div>

        </div>

    );

}

export default Login;