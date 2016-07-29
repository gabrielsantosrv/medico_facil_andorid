package com.medicofacil.medicofacilapp.classesDBO;

import java.io.Serializable;

/**
 * Representa uma relação entre um convênio e uma clínica.
 * @author Equipe do projeto Médico Fácil
 */
public class ConvenioClinica implements Cloneable,Serializable{

    private int id; // Id da relação entre convênio e clínica.
    private int idConvenio; // id do convênio.
    private int idClinica; // id da clinica.

    // Construtor de cópia.
    public ConvenioClinica(ConvenioClinica convenioClinica)throws Exception{
        if (convenioClinica == null)
            throw new Exception("ConvenioClinica não fornecido para construtor de cópia.");
        this.id         = convenioClinica.getId();
        this.idClinica  = convenioClinica.getIdClinica();
        this.idConvenio = convenioClinica.getIdConvenio();
    }

    // Retorna uma cópia dessa classe.
    @Override
    public ConvenioClinica clone(){
        ConvenioClinica convenioClinica = null;
        try{
            convenioClinica = new ConvenioClinica(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return convenioClinica;
    }

    // Construtor polimórfico.
    public ConvenioClinica(int id,
                           int idConvenio,
                           int idClinica)throws Exception{
        this.setId(id);
        this.setIdConvenio(idConvenio);
        this.setIdClinica(idClinica);
    }

    // Construtor default.
    public ConvenioClinica(){
        this.id         = 0;
        this.idConvenio = 0;
        this.idClinica  = 0;
    }

    // Retorna o id da relação entre convênio e clínica.
    public int getId() {
        return id;
    }

    // Seta o id da relação entre convênio e clínica.
    public void setId(int id)throws Exception{
        if (id < 0)
            throw new Exception("Id do ConvenioClinica inválido.");
        this.id = id;
    }

    // Retorna o id do convênio.
    public int getIdConvenio() {
        return idConvenio;
    }

    // Seta o id do convênio.
    public void setIdConvenio(int idConvenio)throws Exception{
        if (idConvenio < 0)
            throw new Exception("Id do convênio inválido.");
        this.idConvenio = idConvenio;
    }

    // Retorna o id da clínica.
    public int getIdClinica() {
        return idClinica;
    }

    // Seta o id da clínica.
    public void setIdClinica(int idClinica)throws Exception{
        if (idClinica < 0)
            throw new Exception("Id da clínica inválido.");
        this.idClinica = idClinica;
    }
}