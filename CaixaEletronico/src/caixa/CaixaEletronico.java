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

public class CaixaEletronico extends JFrame implements ICaixaEletronico {

	private JPanel contentPane;
	// Matriz oficial do seu grupo
	private int[][] cedulas = {
			{100, 100}, {50, 200}, {20, 300}, {10, 350}, {5, 450}, {2, 500}
	};
	
	private int cotaMinima = 0;
	private int limiteMinimo = 45;
	private ArrayList<String> historico = new ArrayList<>();

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

	public CaixaEletronico() {
		setTitle("Sistema de Caixa Eletrônico");
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
				JOptionPane.showMessageDialog(null, sacar(Integer.parseInt(val)));
			}
		});
		pnlCliente.add(btnSaque);
		contentPane.add(pnlCliente);

		// --- Módulo do Administrador ---
		JPanel pnlAdm = new JPanel(new GridLayout(0, 1, 5, 5));
		pnlAdm.setBorder(new TitledBorder("=== Módulo do Administrador ==="));
		
		JButton btnRelatorio = new JButton("Relatório de Cédulas");
		btnRelatorio.addActionListener(e -> JOptionPane.showMessageDialog(null, pegaRelatorioCedulas()));
		
		JButton btnTotal = new JButton("Valor Total Disponível");
		btnTotal.addActionListener(e -> JOptionPane.showMessageDialog(null, pegaValorTotalDisponivel()));

		JButton btnReposicao = new JButton("Reposição de Cédulas");
		btnReposicao.addActionListener(e -> {
			String nota = JOptionPane.showInputDialog("Valor da nota (2, 5, 10, 20, 50, 100):");
			String qtd = JOptionPane.showInputDialog("Quantidade para repor:");
			if (nota != null && qtd != null) {
				JOptionPane.showMessageDialog(null, reposicaoCedulas(Integer.parseInt(nota), Integer.parseInt(qtd)));
			}
		});

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
		JButton btnSair = new JButton("Sair (Ver Extrato)");
		btnSair.addActionListener(e -> {
			JOptionPane.showMessageDialog(null, mostraExtrato());
			System.exit(0);
		});
		pnlOutros.add(btnSair);
		contentPane.add(pnlOutros);
	}

	// --- MÉTODOS DE LÓGICA (COM AS FUNÇÕES COMPLETAS) ---

	public String sacar(Integer valor) {
		if (valor == null || valor <= 0) return "Valor inválido.";
		
		//Bloqueia se o saque for menor que a cota mínima definida
		if (valor < cotaMinima) {
			return "Saque Negado: O valor mínimo para saque é R$ " + cotaMinima;
		}

		int[] notasUsadas = new int[6];
		int valorRestante = valor;
		int totalCedulas = 0;
		int saldoAntes = calcularTotal();

		for (int i = 0; i < cedulas.length; i++) {
			int cabem = Math.min(valorRestante / cedulas[i][0], Math.min(cedulas[i][1], 30 - totalCedulas));
			notasUsadas[i] = cabem;
			valorRestante -= cabem * cedulas[i][0];
			totalCedulas += cabem;
		}

		if (valorRestante == 0) {
			StringBuilder sb = new StringBuilder("Saque efetuado com sucesso!\n\n");
			for (int i = 0; i < cedulas.length; i++) {
				if (notasUsadas[i] > 0) {
					cedulas[i][1] -= notasUsadas[i];
					sb.append(String.format("- %d nota(s) de R$ %d\n", notasUsadas[i], cedulas[i][0]));
				}
			}
			historico.add(String.format("Saque de R$ %d realizado.\n Saldo anterior: R$ %d\n Saldo atual: R$ %d", 
					valor, saldoAntes, calcularTotal()));
			verificaEstoqueBaixo();
			return sb.toString();
		}
		return "Saque não realizado: falta de cédulas ou limite de 30 notas.";
	}

	public String reposicaoCedulas(Integer cedula, Integer quantidade) {
		if (quantidade == null || quantidade <= 0) return "Quantidade inválida.";
		for (int i = 0; i < cedulas.length; i++) {
			if (cedula.equals(cedulas[i][0])) {
				cedulas[i][1] += quantidade;
				return String.format("R$ %d reposicionadas. Novo estoque: %d", cedula, cedulas[i][1]);
			}
		}
		return "Cédula não encontrada.";
	}

	public String pegaRelatorioCedulas() {
		StringBuilder sb = new StringBuilder("--- Relatório de Estoque ---\n");
		for (int[] c : cedulas) sb.append(String.format("Nota de R$ %d: %d unidades\n", c[0], c[1]));
		return sb.toString();
	}

	public String pegaValorTotalDisponivel() {
		return "Total: R$ " + calcularTotal();
	}

	private int calcularTotal() {
		int total = 0;
		for (int[] c : cedulas) total += c[0] * c[1];
		return total;
	}

	public String armazenaCotaMinima(Integer minimo) {
		this.cotaMinima = minimo;
		return "Cota de R$ " + minimo + " definida como valor mínimo de saque!";
	}

	public String mostraExtrato() {
		if (historico.isEmpty()) return "Sem movimentações.";
		StringBuilder sb = new StringBuilder("--- Extrato Final ---\n");
		for (String h : historico) sb.append(h).append("\n------------------\n");
		return sb.toString();
	}

	private void verificaEstoqueBaixo() {
		for (int[] c : cedulas) {
			if (c[1] < limiteMinimo) {
				historico.add(String.format("ALERTA: Cédula de R$ %d abaixo de %d unidades!", c[0], limiteMinimo));
			}
		}
	}
}