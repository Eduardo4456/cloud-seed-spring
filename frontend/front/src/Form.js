function Form({botao, eventoTeclado, cadastrar}) {
    return (
        <form>
            <input type='text' onChange={eventoTeclado} name='name' placeholder="nome (*)" className='form-control'/>
            <input type='text' onChange={eventoTeclado} name='species'  placeholder="especie" className='form-control'/>
            <input type='text' onChange={eventoTeclado} name='location' placeholder="local (*)" className='form-control'/>

            {
                botao
                ?//se for true
                <input type='button' value='cadastrar' onClick={cadastrar} className="btn btn-primary"></input>
                ://se não
                <div>
                    <input type='button' value='alterar' className="btn btn-warning"></input>
                    <input type='button' value='deletar' className="btn btn-danger"></input>
                    <input type='button' value='cancelar'
                    className="btn btn-secondary"></input>
                </div>
            }
            
        </form>
    )
}

export default Form;