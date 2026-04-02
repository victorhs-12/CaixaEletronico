package caixa;

public class CaixaEletronico implements ICaixaEletronico{
	//aqui vai ser o coração do caixa eletrônico, porque neste array, está guardado as notas e a quantidade total delas
	private int[][] cedulas = {
			{100, 100},
			{50, 200},
			{20, 300},
			{10, 350},
			{5, 450},
			{2, 500}
	};
	
	private int cotaMinima = 0;
	
	//neste metodo, esse loop vai ler todo o array que está guardado as notas e a quantidade delas, e vai mostrar to
	public String pegaValorTotalDisponivel() {
		int total = 0;
		for(int i = 0; i < cedulas.length; i++ ) {
			total += cedulas[i][0] * cedulas[i][1];
		}
		return "Total: R$ " + total;
	}
	
	//neste método, ele percorre toda a matriz onde está as cédulas, e eu coloquei o return dentro do loop para moostar todas as cédulas e a quantidade delas
	public String pegaRelatorioCedulas() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < cedulas.length; i++) {
			sb.append(String.format("Nota de R$ %d: %d unidades\n", cedulas[i][0], cedulas[i][1]));
		}
		return sb.toString();
	}
	//neste método vai armazenar a cota mínima. Ela vai ser útil no método de saque
	public String armazenaCotaMinima(Integer minimo) {
		this.cotaMinima = minimo;
		
		return "Cota mínima armazenada com sucesso!";
	}
	
	/*neste método, ele percorre toda a matriz com o loop for, para encontrar a cédula que tem uma quantidade menor. E quando encontra
	 * a cédula, coloquei uma boolean, com o intuito de assegurar que este método mande a mensagem correta
	 */
	public String reposicaoCedulas(Integer cedula, Integer quantidade) {
		boolean encontrou = false;
		for(int i = 0; i < cedulas.length; i++) {
			if(cedula.equals(cedulas[i][0])) {
				cedulas[i][1] += quantidade;
				encontrou = true;
				break;
			}
		}
		if(encontrou) {
			return String.format("Cédulas de R$%d reposicionadas\n Quantidade de cédulas reposicionadas: %d cédulas", cedula, quantidade);
		}
		else {
			return "Cédula não encontrada";
		}
	}
}