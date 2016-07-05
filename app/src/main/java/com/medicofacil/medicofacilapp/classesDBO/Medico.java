package com.medicofacil.medicofacilapp.classesDBO;

import com.medicofacil.medicofacilapp.classesValidacao.Validacao;

import java.io.Serializable;

/**
 * Representa um determinado médico.
 * @author Equipe do projeto Médico Fácil
*/
public class Medico implements Cloneable,Serializable{
    
  private int id; // Id do médico.
  private String especialidade; // Id da especialidade médica.
  private String uf; // Sigla do estado em que o médico atua.
  private String crm;  // Identificação do médico.
  private String nome; // Nome do médico.
  
  private final int MAX_NOME = 50; // Máximo de caracteres para o nome do médico.
  private final int MAX_UF = 2;
  private final int MAX_ESPECIALIDADE = 30;
  
  // Retorna o id do médico.
  public int getId() {
    return id;
  }

  // Seta o id do médico.
  public void setId(int id)throws Exception{
    if (id < 0)
      throw new Exception("Id do médico inválido.");
    this.id = id;
  }

  // Retorna a especialidade médica.
  public String getEspecialidade() {
    return this.especialidade;
  }

  // Seta a especialidade médica.
  public void setEspecialidade(String especialidade)throws Exception{
    if (!Validacao.isPalavraValida(especialidade,this.MAX_ESPECIALIDADE))
      throw new Exception("Especialidade inválida.");
    this.especialidade = especialidade;
  }

  // Retorna a sigla do estado.
  public String getUf() {
    return this.uf;
  }

  // Seta a sigla do estado.
  public void setUf(String uf)throws Exception{
    if (!Validacao.isSiglaValida(uf,this.MAX_UF))
      throw new Exception("Sigla do estado inválida.");
    this.uf = uf;
  }

  // Retorna o crm do médico.
  public String getCrm(){
    return this.crm;
  }

  // Seta o crm do médico.
  public void setCrm(String crm)throws Exception{
    if (!Validacao.isCrmValido(crm))
      throw new Exception("Crm do médico inválido.");
    this.crm = crm;
  }

  // Retorna o nome do médico.
  public String getNome(){
    return nome;
  }

  // Seta o nome do médico.
  public void setNome(String nome)throws Exception{
    if (!Validacao.isNomePessoaValido(nome,this.MAX_NOME))
      throw new Exception("Nome do médico inválido.");
    this.nome = nome;
  }


  
  // Construtor de cópia.
  public Medico(Medico medico)throws Exception{
    if (medico == null)
      throw new Exception("Médico não fornecido para construtor de cópia.");
    this.id            = medico.getId();
    this.especialidade = medico.getEspecialidade();
    this.uf            = medico.getUf();
    this.crm           = medico.getCrm();
    this.nome          = medico.getNome();
  }
  
  // Retorna uma cópia desse médico.
  @Override
  public Medico clone(){
    Medico medico = null;
    try{
      medico = new Medico(this);
    }
    catch (Exception e){
      e.printStackTrace();
    }
    return medico;
  }
  
  // Construor default.
  public Medico(){
    this.id              = 0;
    this.especialidade   = "";
    this.uf              = "";
    this.crm             = "";
    this.nome            = "";
  }
  
  // Construtor polimórfico.
  public Medico(int id,
                String especialidade,
                String uf,
                String crm,
                String nome)throws Exception{
    this.setId(id);
    this.setEspecialidade(especialidade);
    this.setUf(uf);
    this.setCrm(crm);
    this.setNome(nome);
  }
  
  // Retorna uma string relacionada à esse médico.
  @Override
  public String toString(){
    return this.crm;
  }
}
