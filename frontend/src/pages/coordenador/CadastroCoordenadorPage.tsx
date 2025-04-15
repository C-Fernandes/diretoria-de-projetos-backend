import React, { useState } from 'react';
import axios from 'axios';

function CadastroCoordenadorPage() {
    const [formData, setFormData] = useState({
        matricula: '',
        name: '',
        email: '',
        unidadeAcademica: '',
    });


    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev, [name]: value
        }));
    }
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        try {
            await axios.post('http://localhost:8080/coordenadores/register', formData);
            alert('Coordenador cadastrado com sucesso!');
            setFormData({
                matricula: '', name: '', email: '', unidadeAcademica: ''
            });
        } catch (error) {
            console.log(error);
            alert('Erro ao cadastrar coordenador.');
        }
    };
    return (<>
        <form onSubmit={handleSubmit}>
            <div>
                <label>Matrícula:</label>
                <input
                    type="text"
                    name="matricula"
                    value={formData.matricula}
                    onChange={handleChange}
                    required
                />
            </div>

            <div>
                <label>Nome:</label>
                <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                />
            </div>

            <div>
                <label>Email:</label>
                <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                />
            </div>

            <div>
                <label>Unidade Acadêmica:</label>
                <input
                    type="text"
                    name="unidadeAcademica"
                    value={formData.unidadeAcademica}
                    onChange={handleChange}
                    required
                />
            </div>

            <button type="submit">Cadastrar Coordenador</button>
        </form>

    </>);
};

export default CadastroCoordenadorPage;