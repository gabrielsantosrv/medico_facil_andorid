package com.medicofacil.medicofacilapp.classesDBO;

import com.medicofacil.medicofacilapp.classesValidacao.Validacao;

import java.util.Objects;

/**
  * Representa uma especialidade médica.
  * @author Equipe do projeto Médico Fácil
*/
public class Especialidade implements Cloneable{
   
  private int id; // Id da especialidade médica.
  private String nome; // Nome da especialidade médica.
  
  // Número máximo de caracteres para o nome da especialidade médica.
  private final int MAX_NOME = 20; 


  // Construtor de cópia.
  public Especialidade(Especialidade especialidade)throws Exception{
    if (especialidade == null)
      throw new Exception("Especialidade médica não fornecida para construtor de cópia.");
    this.id   = especialidade.getId();
    this.nome = especialidade.getNome();
  }
  
  // Retorna uma cópia dessa especialidade médica.
  @Override
  public Especialidade clone(){
    Especialidade especialidade = null;
    try{
      especialidade = new Especialidade(this);
    }
    catch (Exception e){
      e.printStackTrace();
    }
    return especialidade;
  }
  
  // Construtor polimórfico.
  public Especialidade(int id,String nome)throws Exception{
    this.setId(id);
    this.setNome(nome);
  }
  
  // Construtor default.
  public Especialidade(){
    this.id   = 0;
    this.nome = "";
  }

  // Retorna o id dessa especialidade médica.
  public int getId(){
    return id;
  }

  // Seta o id dessa especialidade médica.
  public void setId(int id) throws Exception{
    if (id < 0)
      throw new Exception("Id da especialidade médica inválido.");
    this.id = id;
  }

  // Retorna o nome dessa especialidade médica.
  public String getNome(){
    return nome;
  }

  // Seta o nome dessa especialidade médica.
  public void setNome(String nome) throws Exception{
    if (!Validacao.isPalavraValida(nome,this.MAX_NOME))
      throw new Exception("Nome da especialidade médica inválido.");
    this.nome = nome;
  }
  
  // Retorna uma string relacionada à essa especialidade médica.
  @Override
  public String toString(){
    return this.nome;
  }
}
