package caixa;

public interface ICaixaEletronico {
	
	public String pegaValorTotalDisponivel();
	
	public String sacar(Integer valor);
	
	public String pegaRelatorioCedulas();
	
	public String reposicaoCedulas(Integer cedula, Integer quantidade);
	
	public String armazenaCotaMinima(Integer minimo);
}
