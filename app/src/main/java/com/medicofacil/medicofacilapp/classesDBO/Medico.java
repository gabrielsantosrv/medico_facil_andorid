package com.medicofacil.medicofacilapp.classesDBO;

import com.medicofacil.medicofacilapp.classesValidacao.Validacao;

/**
 * Representa um determinado médico.
 * @author Equipe do projeto Médico Fácil
*/
public class Medico implements Cloneable{
    
  private int id; // Id do médico.
  private String especialidade;
  private String uf; // estado em que o médico atua.
  private String crm;  // Identificação do médico.
  private String nome; // Nome do médico.
  
  private final int MAX_NOME = 50; // Máximo de caracteres para o nome do médico.
  
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

  public String getEspecialidade() {
    return especialidade;
  }

  public void setEspecialidade(String especialidade) {
    this.especialidade = especialidade;
  }

  public String getUf() {
    return uf;
  }

  public void setUf(String uf) {
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
    this.id              = medico.getId();
    this.uf              = medico.getUf();
    this.especialidade   = medico.getEspecialidade();
    this.crm             = medico.getCrm();
    this.nome            = medico.getNome();
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
    this.setUf(uf);
    this.setEspecialidade(especialidade);
    this.setCrm(crm);
    this.setNome(nome);
  }
  
  // Retorna uma string relacionada à esse médico.
  @Override
  public String toString(){
    return this.crm;
  }
}
