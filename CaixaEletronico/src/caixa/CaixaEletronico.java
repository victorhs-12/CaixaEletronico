package caixa;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Classe principal do Caixa Eletrônico.
 * Estende JFrame para criar a interface gráfica e implementa ICaixaEletronico para as regras de negócio.
 */
public class CaixaEletronico extends JFrame implements ICaixaEletronico {

	private JPanel contentPane;
	
	/**
	 * Matriz de cédulas: [valor da nota, quantidade em estoque].
	 * Representa o dinheiro disponível fisicamente no caixa.
	 */
	private int[][] cedulas = {
			{100, 100}, {50, 200}, {20, 300}, {10, 350}, {5, 450}, {2, 500}
	};
	
	private int cotaMinima = 0; // Valor mínimo que o cliente pode sacar
	private int limiteMinimo = 45; // Alerta quando o estoque de uma nota cai abaixo deste valor
	private ArrayList<String> historico = new ArrayList<>(); // Armazena registros de saques e alertas

	/**
	 * Ponto de entrada do sistema. Inicia a interface gráfica.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				CaixaEletronico frame = new CaixaEletronico();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Construtor da classe: Configura o layout da janela e cria os botões.
	 */
	public CaixaEletronico() {
		setTitle("Sistema de Caixa Eletrônico - Conclusão");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 10, 10));

		// --- Módulo do Cliente ---
		JPanel pnlCliente = new JPanel(new GridLayout(0, 1, 5, 5));
		pnlCliente.setBorder(new TitledBorder("=== Módulo do Cliente ==="));
		
		JButton btnSaque = new JButton("Efetuar Saque");
		btnSaque.addActionListener(e -> {
			String val = JOptionPane.showInputDialog("Digite o valor do saque:");
			if (val != null && !val.isEmpty()) {
				// Chama a função sacar e exibe o resultado (sucesso ou erro)
				JOptionPane.showMessageDialog(null, sacar(Integer.parseInt(val)));
			}
		});
		pnlCliente.add(btnSaque);
		contentPane.add(pnlCliente);

		// --- Módulo do Administrador ---
		JPanel pnlAdm = new JPanel(new GridLayout(0, 1, 5, 5));
		pnlAdm.setBorder(new TitledBorder("=== Módulo do Administrador ==="));
		
		// Botão para ver quantas notas de cada valor existem no caixa
		JButton btnRelatorio = new JButton("Relatório de Cédulas");
		btnRelatorio.addActionListener(e -> JOptionPane.showMessageDialog(null, pegaRelatorioCedulas()));
		
		// Botão para ver o valor total em reais dentro do caixa
		JButton btnTotal = new JButton("Valor Total Disponível");
		btnTotal.addActionListener(e -> JOptionPane.showMessageDialog(null, pegaValorTotalDisponivel()));

		// Botão para o gerente adicionar mais notas ao caixa
		JButton btnReposicao = new JButton("Reposição de Cédulas");
		btnReposicao.addActionListener(e -> {
			String nota = JOptionPane.showInputDialog("Valor da nota (2, 5, 10, 20, 50, 100):");
			String qtd = JOptionPane.showInputDialog("Quantidade para repor:");
			if (nota != null && qtd != null) {
				JOptionPane.showMessageDialog(null, reposicaoCedulas(Integer.parseInt(nota), Integer.parseInt(qtd)));
			}
		});

		// Botão para definir o valor mínimo de saque (Cota)
		JButton btnCota = new JButton("Definir Cota Mínima");
		btnCota.addActionListener(e -> {
			String val = JOptionPane.showInputDialog("Defina o valor mínimo de saque:");
			if (val != null) JOptionPane.showMessageDialog(null, armazenaCotaMinima(Integer.parseInt(val)));
		});

		pnlAdm.add(btnRelatorio);
		pnlAdm.add(btnTotal);
		pnlAdm.add(btnReposicao);
		pnlAdm.add(btnCota);
		contentPane.add(pnlAdm);

		// --- Outros ---
		JPanel pnlOutros = new JPanel(new GridLayout(0, 1, 5, 5));
		pnlOutros.setBorder(new TitledBorder("=== Outros ==="));
		
		// Botão para fechar o sistema e mostrar o que aconteceu (extrato)
		JButton btnSair = new JButton("Sair (Ver Extrato)");
		btnSair.addActionListener(e -> {
			JOptionPane.showMessageDialog(null, mostraExtrato());
			System.exit(0);
		});
		pnlOutros.add(btnSair);
		contentPane.add(pnlOutros);
	}

	// --- MÉTODOS DE LÓGICA DE NEGÓCIO ---

	/**
	 * Processa a retirada de dinheiro.
	 * Valida o valor, a cota mínima, o estoque e o limite de 30 notas.
	 */
	public String sacar(Integer valor) {
		// Validação inicial: evita valores nulos ou negativos
		if (valor == null || valor <= 0) return "Valor inválido.";
		
		// Regra da Cota Mínima: Bloqueia saques menores que o definido pelo administrador
		if (valor < cotaMinima) {
			return "Saque Negado: O valor mínimo para saque é R$ " + cotaMinima;
		}

		int[] notasUsadas = new int[6]; // Guarda a quantidade de cada nota que será entregue
		int valorRestante = valor;
		int totalCedulas = 0; // Contador para não estourar o limite de 30 notas
		int saldoAntes = calcularTotal();

		// Algoritmo Guloso: Tenta usar as notas maiores primeiro (100 -> 50 -> 20...)
		for (int i = 0; i < cedulas.length; i++) {
			int valorNota = cedulas[i][0];
			int estoqueNota = cedulas[i][1];
			
			// Calcula quantas notas cabem no valor restante, no estoque e no limite de 30
			int cabem = Math.min(valorRestante / valorNota, Math.min(estoqueNota, 30 - totalCedulas));
			
			notasUsadas[i] = cabem;
			valorRestante -= cabem * valorNota;
			totalCedulas += cabem;
		}

		// Se o valorRestante for 0, o saque é possível
		if (valorRestante == 0) {
			StringBuilder sb = new StringBuilder("Saque efetuado com sucesso!\n\n");
			for (int i = 0; i < cedulas.length; i++) {
				if (notasUsadas[i] > 0) {
					cedulas[i][1] -= notasUsadas[i]; // Remove as notas do estoque físico
					sb.append(String.format("- %d nota(s) de R$ %d\n", notasUsadas[i], cedulas[i][0]));
				}
			}
			// Registra a transação no histórico
			historico.add(String.format("Saque de R$ %d realizado.\n Saldo anterior: R$ %d\n Saldo atual: R$ %d", 
					valor, saldoAntes, calcularTotal()));
			
			verificaEstoqueBaixo(); // Checa se alguma nota acabou
			return sb.toString();
		}
		
		// Se sobrou valorRestante, o caixa não tinha as notas necessárias
		return "Saque não realizado: falta de cédulas ou limite de 30 notas excedido.";
	}

	/**
	 * Adiciona novas notas ao estoque do caixa.
	 */
	public String reposicaoCedulas(Integer cedula, Integer quantidade) {
		if (quantidade == null || quantidade <= 0) return "Quantidade inválida.";
		
		for (int i = 0; i < cedulas.length; i++) {
			if (cedula.equals(cedulas[i][0])) {
				cedulas[i][1] += quantidade; // Soma ao estoque existente
				return String.format("R$ %d reposicionadas. Novo estoque: %d", cedula, cedulas[i][1]);
			}
		}
		return "Cédula não encontrada.";
	}

	/**
	 * Gera o relatório de quantas notas de cada valor estão no caixa.
	 */
	public String pegaRelatorioCedulas() {
		StringBuilder sb = new StringBuilder("--- Relatório de Estoque ---\n");
		for (int[] c : cedulas) {
			sb.append(String.format("Nota de R$ %d: %d unidades\n", c[0], c[1]));
		}
		return sb.toString();
	}

	/**
	 * Exibe o valor total somado de todas as notas no caixa.
	 */
	public String pegaValorTotalDisponivel() {
		return "Total disponível no caixa: R$ " + calcularTotal();
	}

	/**
	 * Função auxiliar para calcular a soma financeira total da matriz.
	 */
	private int calcularTotal() {
		int total = 0;
		for (int[] c : cedulas) {
			total += c[0] * c[1];
		}
		return total;
	}

	/**
	 * Armazena o limite de saque definido pelo administrador.
	 */
	public String armazenaCotaMinima(Integer minimo) {
		this.cotaMinima = minimo;
		return "Configuração salva: O valor mínimo de saque agora é R$ " + minimo;
	}

	/**
	 * Retorna o histórico de todas as operações realizadas desde que o sistema abriu.
	 */
	public String mostraExtrato() {
		if (historico.isEmpty()) return "Sem movimentações registradas.";
		
		StringBuilder sb = new StringBuilder("--- Extrato de Operações ---\n");
		for (String h : historico) {
			sb.append(h).append("\n------------------\n");
		}
		return sb.toString();
	}

	/**
	 * Monitora o estoque e gera alertas automáticos no histórico se uma nota baixar de 45 unidades.
	 */
	private void verificaEstoqueBaixo() {
		for (int[] c : cedulas) {
			if (c[1] < limiteMinimo) {
				historico.add(String.format("ALERTA CRÍTICO: Cédula de R$ %d abaixo de %d unidades! (Restam: %d)", 
						c[0], limiteMinimo, c[1]));
			}
		}
	}
}