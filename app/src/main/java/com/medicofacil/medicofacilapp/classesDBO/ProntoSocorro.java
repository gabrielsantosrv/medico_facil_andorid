package com.medicofacil.medicofacilapp.classesDBO;


import com.medicofacil.medicofacilapp.classesValidacao.Validacao;

/**
 * Representa um determinado pronto socorro.
 * @author Equipe do projeto Médico Fácil
*/
public class ProntoSocorro implements Cloneable{
  
  private int id,idCidade;
  private String bairro,endereco,nome,telefone;
  
  // Número máximo de caracteres para alguns atributos.
  private final int MAX_BAIRRO   = 30;
  private final int MAX_ENDERECO = 100;
  private final int MAX_NOME     = 30;
  private final int MAX_TELEFONE = 10;

  
  // Construtor de cópia.
  public ProntoSocorro(ProntoSocorro prontoSocorro)throws Exception{
    if (prontoSocorro == null)
      throw new Exception("ProntoSocorro não fornecido em construtor de cópia.");
    this.id       = prontoSocorro.getId();
    this.idCidade = prontoSocorro.getIdCidade();
    this.bairro   = prontoSocorro.getBairro();
    this.endereco = prontoSocorro.getEndereco();
    this.nome     = prontoSocorro.getNome();
    this.telefone = prontoSocorro.getTelefone();
  }
  
  // Retorna uma cópia desse ProntoSocorro.
  @Override
  public ProntoSocorro clone(){
    ProntoSocorro prontoSocorro = null;
    try{
      prontoSocorro = new ProntoSocorro(this);
    }
    catch (Exception e){
      e.printStackTrace();
    }
    return prontoSocorro;
  }
  
  // Construtor polimórfico.
  public ProntoSocorro(int id,
                       int idCidade,
                       String bairro,
                       String endereco,
                       String nome,
                       String telefone)throws Exception{
    this.setId(id);
    this.setIdCidade(idCidade);
    this.setBairro(bairro);
    this.setEndereco(endereco);
    this.setNome(nome);
    this.setTelefone(telefone);
  }
  
  // Construtor default.
  public ProntoSocorro(){
    this.id       = 0;
    this.idCidade = 0;
    this.bairro   = "";
    this.endereco = "";
    this.nome     = "";
    this.telefone = "";
  }
  
  // Seta o id do pronto socorro.
  public void setId(int id)throws Exception{
    if (id < 0)
      throw new Exception("Id do pronto socorro inválido.");
    this.id = id;
  }
  
  // Seta o id da cidade em que o pronto socorro se localiza.
  public void setIdCidade(int idCidade)throws Exception{
    if (idCidade < 0)
      throw new Exception("Id da cidade inválido.");
    this.idCidade = idCidade;
  }
  
  // Seta o bairro em que o pronto socorro se localiza.
  public void setBairro(String bairro)throws Exception{
    if (!Validacao.isNomeLocalizacaoValido(bairro,this.MAX_BAIRRO))
      throw new Exception("Bairro inválido.");
    this.bairro = bairro;
  }
  
  // Seta o endereço do pronto socorro.
  public void setEndereco(String endereco)throws Exception{
    if (!Validacao.isEnderecoValido(endereco,this.MAX_ENDERECO))
      throw new Exception("Endereço inválido.");
    this.endereco = endereco;
  }
  
  // Seta o nome do pronto socorro.
  public void setNome(String nome)throws Exception{
    if (!Validacao.isNomeInstituicaoValido(nome,this.MAX_NOME))
      throw new Exception("Nome do pronto socorro inválido.");
    this.nome = nome;
  }
  
  // Seta o telefone do pronto socorro.
  public void setTelefone(String telefone)throws Exception{
    if (!Validacao.isTelefoneValido(telefone))
      throw new Exception("Telefone inválido.");
    this.telefone = telefone;
  }
  
  // Retorna o id do pronto socorro.
  public int getId(){
    return this.id;
  }
  
  // Retorna o id da cidade em que o pronto socorro se localiza.
  public int getIdCidade(){
    return this.idCidade;
  }
  
  // Retorna o bairro em que o pronto socorro se localiza.
  public String getBairro(){
    return this.bairro;
  }
  
  // Retorna o endereço do pronto socorro.
  public String getEndereco(){
    return this.endereco;
  }
  
  // Retorna o nome do pronto socorro.
  public String getNome(){
    return this.nome;
  }
  
  // Retorna o telefone do pronto socorro.
  public String getTelefone(){
    return this.telefone;
  }
  
  // Retorna uma string que representa esse pronto socorro.
  @Override
  public String toString(){
    return this.nome; 
  }
}
