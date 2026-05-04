package caixa;

import java.util.ArrayList;

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
	private int limiteMinimo = 45;
	private ArrayList<String> historico = new ArrayList<>();
	
	private int calcularTotal() {
		int totalCaixa = 0;
		for(int i = 0; i < cedulas.length; i++) {
			totalCaixa += cedulas[i][0] * cedulas[i][1];
			}
		
		return totalCaixa;
	}

	public String pegaValorTotalDisponivel() {
		return "Total: R$ " + calcularTotal();
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
		if(quantidade == null || quantidade == 0 || quantidade < 0) {
			return "Não vai ser possível fazer a reposição de cédulas. Tente novamente";
		}
		
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

	public String sacar(Integer valor) {
		if(valor == null || valor == 0 || valor < 0) {
			return "Impossível realizar o saque deste valor. Tente novamente";
		}
		if(valor < cotaMinima) {
			return "Não é possivel fazer o saque, pois o valor mínimo para retirar é de R$ " + cotaMinima;
		}
		
		int [] notasUsadas = new int[6];
		int valorRestante = valor;
		int totalCedulas = 0;
		int saldoAntes = calcularTotal();
		
		for(int i = 0; i < cedulas.length; i++) {
			int cabemPeloValor = valorRestante / cedulas[i][0];
			int cabemPeloEstoque = cedulas[i][1];
			int cabemPeloLimite = 30 - totalCedulas;
			
			
			int notasAUsar = Math.min(cabemPeloValor, Math.min(cabemPeloEstoque, cabemPeloLimite));

			notasUsadas[i] = notasAUsar;
			valorRestante -= notasAUsar * cedulas[i][0];
			totalCedulas += notasAUsar;
			
		}

		if(valorRestante == 0) {
			StringBuilder sb2 = new StringBuilder();
			for(int i = 0; i < cedulas.length; i++) {
				cedulas[i][1] -= notasUsadas[i];

			}
			for(int i = 0; i < cedulas.length; i++) {
				if(notasUsadas[i] > 0) {
					sb2.append(String.format("Quantidade de R$ %d usadas: %d unidades\n", cedulas[i][0], notasUsadas[i]));
					
				}
			}
			historico.add(String.format("Saque realizado no valor de R$ %d\n Saldo anterior: R$ %d\n Saldo atual: R$ %d", valor, saldoAntes, calcularTotal()));
			verificaEstoqueBaixo();
			return sb2.toString();
			
		}
		else {
			return "Saque não realizado por falta de cédulas";
		}
		
		
	}
	
	public String mostraExtrato() {
		StringBuilder sb3 = new StringBuilder();
		if(historico.isEmpty()) {
			return "Não houve movimentações nesta conta";
		}
		else {
			for(String movimentacao : historico) {
				sb3.append(movimentacao);
				sb3.append(String.format("\n------------------------------\n"));
			}
		}
		return sb3.toString();
	}
	
	
	private void verificaEstoqueBaixo() {

		
		for(int i = 0; i < cedulas.length; i++) {
			if(cedulas[i][1] < limiteMinimo) {
				historico.add(String.format("A cédula de R$ %d precisa ser recarregada. Quantidade atual: %d unidades", cedulas[i][0], cedulas[i][1]));
			}
		}
	}
	
	
	public static void main(String[] args) {
		//aqui vai chamar a interface com o GUI

	}
}
