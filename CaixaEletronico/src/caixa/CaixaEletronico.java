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
	
	public String pegaRelatorioCedulas() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < cedulas.length; i++) {
			sb.append(String.format("Nota de R$ %d: %d unidades\n", cedulas[i][0], cedulas[i][1]));
		}
		return sb.toString();
	}
}