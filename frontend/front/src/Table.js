function Table({vetor}) {
    return (
        <table className="table">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Nome</th>
                    <th>Especie</th>
                    <th>Local</th>
                    <th>Data criação</th>
                    <th>Selecionar</th>
                </tr>
            </thead>

            <tbody>
               {
                vetor.map((obj, indice) =>  (
                     <tr key={indice}>
                        <td>{obj.id}</td>
                        <td>{obj.name}</td>
                        <td>{obj.species}</td>
                        <td>{obj.location}</td>
                        <td>{obj.createdAt}</td>
                        <td><button className="btn btn-success">Selecionar</button></td>
                    </tr>
                ))
               }
            </tbody>
        </table>
    )
}

export default Table;