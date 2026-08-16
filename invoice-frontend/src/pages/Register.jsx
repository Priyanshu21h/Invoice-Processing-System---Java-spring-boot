import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { registerUser } from "../api/authApi";

function Register() {

    const navigate = useNavigate();

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

            await registerUser(formData);

            toast.success("Registration successful.");

            navigate("/login");

        } catch (error) {

            console.log(error);

            toast.error(
                error.response?.data?.message || "Registration failed."
            );

        }

    };

    return (

        <div className="login-container">

            <div className="login-card">

                <h1>Create Account</h1>

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
                        Register
                    </button>

                </form>

            </div>

        </div>

    );

}

export default Register;