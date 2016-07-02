package com.medicofacil.medicofacilapp.classesDBO;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.medicofacil.medicofacilapp.classesValidacao.Validacao;


/**
  * Representa uma determinada consulta na clínica.
  * @author Equipe do projeto Médico Fácil
*/
public class Consulta implements Cloneable,Serializable{
  
  private int id; // Id dessa consulta.
  private int idMedicoClinica; // Id da relação entre um médico e uma clínica.
  private int idPaciente; // Id do paciente a ser consultado.
  @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm")
  private Timestamp dataHora; // Data e hora da consulta.
  
  // Construtor de cópia.
  public Consulta(Consulta consulta)throws Exception{
    if (consulta == null)
      throw new Exception("Consulta não fornecida em construtor de cópia.");
    this.id              = consulta.getId();
    this.idMedicoClinica = consulta.getIdMedicoClinica();
    this.idPaciente      = consulta.getIdPaciente();
    this.dataHora        = consulta.getDataHora();
  }
  
  // Retorna uma cópia dessa consulta.
  @Override
  public Consulta clone(){
    Consulta consulta = null;
    try{
      consulta = new Consulta(this);
    }
    catch (Exception e){
      e.printStackTrace();
    }
    return consulta;
  }
  
  // Constutor polimórfico.
  public Consulta(int id,
                  int idMedicoClinica,
                  int idPaciente,
                  Timestamp dataHora)throws Exception{
    this.setId(id);
    this.setIdMedicoClinica(idMedicoClinica);
    this.setIdPaciente(idPaciente);
    this.setDataHora(dataHora);
  }
  
  // Construtor default.
  public Consulta(){
    this.id              = 0;
    this.idMedicoClinica = 0;
    this.idPaciente      = 0;
    this.dataHora        = null;
  }
  
  // Retorna o id dessa consulta.
  public int getId(){
    return id;
  }

  // Seta o id dessa consulta.
  public void setId(int id)throws Exception{
    if (id < 0)
      throw new Exception("Id da consulta inválido.");
    this.id = id;
  }

  // Retorna o id da relação entre médico e clínica.
  public int getIdMedicoClinica() {
    return idMedicoClinica;
  }

  // Seta o id da relação entre médico e clínica.
  public void setIdMedicoClinica(int idMedicoClinica)throws Exception{
    if (idMedicoClinica < 0)
      throw new Exception("Id da relação entre médico e clínica inválido.");
    this.idMedicoClinica = idMedicoClinica;
  }

  // Retorna o id do paciente.
  public int getIdPaciente(){
    return idPaciente;
  }

  // Seta o id do paciente.
  public void setIdPaciente(int idPaciente)throws Exception{
    if (idPaciente < 0)
      throw new Exception("Id do paciente inválido.");
    this.idPaciente = idPaciente;
  }

  // Retorna o período da consulta do paciente.
  @JsonSerialize(using=CustomTimestampSerializer.class)
  public Timestamp getDataHora(){
    return dataHora;
  }

  // Seta o período da consulta do paciente.
  @JsonDeserialize(using=CustomTimestampDeserializer.class)
  public void setDataHora(Timestamp dataHora)throws Exception{
    if (!Validacao.isDataPosteriorValida(dataHora))
      throw new Exception("Período da consulta inválido.");
    this.dataHora = dataHora;
  }
}