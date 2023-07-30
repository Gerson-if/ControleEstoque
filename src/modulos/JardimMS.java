package modulos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class JardimMS extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private List<Produto> produtos;

    public JardimMS() {

        // Define o ícone da janela
        ImageIcon icon = new ImageIcon(getClass().getResource("/icones/cidade.png"));
        setIconImage(icon.getImage());

        initComponents();
        createProductTable();
        produtos = new ArrayList<>();
        loadData();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
        // Centraliza a janela no meio da tela
        centralizarJanela();
    }

    // Função de centralização da janela
    private void centralizarJanela() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        int posX = (screenWidth - windowWidth) / 2;
        int posY = (screenHeight - windowHeight) / 2;
        setLocation(posX, posY);
    }

    private void initComponents() {
        setTitle("Estoque em Jardim"); // Define o título da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 400));

        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnIncrementar = new JButton("+");
        JButton btnDecrementar = new JButton("-");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnImprimir = new JButton("Imprimir Relatório");
        JButton btnPesquisar = new JButton("Pesquisar");

        btnNovo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                novoProduto();
            }
        });
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editarProduto();
            }
        });
        btnIncrementar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                incrementarQuantidade();
            }
        });
        btnDecrementar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                decrementarQuantidade();
            }
        });
        btnExcluir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                excluirProduto();
            }
        });
        btnImprimir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                imprimirRelatorio();
            }
        });
        btnPesquisar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pesquisarProduto();
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(btnNovo);
        buttonsPanel.add(btnEditar);
        buttonsPanel.add(btnIncrementar);
        buttonsPanel.add(btnDecrementar);
        buttonsPanel.add(btnExcluir);
        buttonsPanel.add(btnImprimir);
        buttonsPanel.add(btnPesquisar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonsPanel, BorderLayout.PAGE_START);
        pack();
    }

    private void createProductTable() {
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Produto");
        tableModel.addColumn("Quantidade");
        tableModel.addColumn("Valor");

        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
    

private void novoProduto() {
    JTextField nomeField = new JTextField();
    JTextField quantidadeField = new JTextField();
    JTextField valorField = new JTextField();

    Object[] message = { "Nome:", nomeField, "Quantidade:", quantidadeField, "Valor:", valorField };

    int option = JOptionPane.showConfirmDialog(this, message, "Novo Produto", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        String nome = nomeField.getText().trim();
        String quantidadeText = quantidadeField.getText().trim();
        String valorText = valorField.getText().trim();

        if (!nome.isEmpty() && !quantidadeText.isEmpty() && !valorText.isEmpty()) {
            try {
                int quantidade = Integer.parseInt(quantidadeText);
                double valor = Double.parseDouble(valorText);

                if (!produtoDuplicado(nome)) {
                    addProduct(nome, quantidade, valor);
                } else {
                    JOptionPane.showMessageDialog(this, "O produto já está na lista.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valores inválidos. Por favor, insira valores numéricos válidos.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
        }
    }
}


    private boolean produtoDuplicado(String nome) {
        for (Produto produto : produtos) {
            if (produto.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

 private void editarProduto() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow != -1) {
        Produto produto = produtos.get(selectedRow);

        JTextField nomeField = new JTextField(produto.getNome());
        JTextField quantidadeField = new JTextField(Integer.toString(produto.getQuantidade()));
        JTextField valorField = new JTextField(Double.toString(produto.getValor()));

        Object[] message = { "Nome:", nomeField, "Quantidade:", quantidadeField, "Valor:", valorField };

        int option = JOptionPane.showConfirmDialog(this, message, "Editar Produto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText();
            int quantidade = Integer.parseInt(quantidadeField.getText());
            double valor = Double.parseDouble(valorField.getText());

            produto.setNome(nome);
            produto.setQuantidade(quantidade);
            produto.setValor(valor);

            tableModel.setValueAt(nome, selectedRow, 0);
            tableModel.setValueAt(quantidade, selectedRow, 1);
            tableModel.setValueAt(valor, selectedRow, 2);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Selecione um produto para editar.");
    }
}

    

    private void incrementarQuantidade() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Produto produto = produtos.get(selectedRow);
            produto.setQuantidade(produto.getQuantidade() + 1);
            tableModel.setValueAt(produto.getQuantidade(), selectedRow, 1);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para incrementar a quantidade.");
        }
    }

    private void decrementarQuantidade() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Produto produto = produtos.get(selectedRow);
            if (produto.getQuantidade() > 0) {
                produto.setQuantidade(produto.getQuantidade() - 1);
                tableModel.setValueAt(produto.getQuantidade(), selectedRow, 1);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para decrementar a quantidade.");
        }
    }

    private void excluirProduto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int option = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir o produto selecionado?",
                    "Excluir Produto", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                produtos.remove(selectedRow);
                tableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
        }
    }
    
    

    private void addProduct(String nome, int quantidade, double valor) {
        Object[] row = { nome, quantidade, valor };
        tableModel.addRow(row);
        produtos.add(new Produto(nome, quantidade, valor));
    }

    private void loadData() {
        try {
            FileReader fileReader = new FileReader("dados1.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String nome = parts[0];
                    int quantidade = Integer.parseInt(parts[1]);
                    double valor = Double.parseDouble(parts[2]);
                    if (!produtoDuplicado(nome)) {
                        addProduct(nome, quantidade, valor);
                    }
                }
            }

            bufferedReader.close();
            fileReader.close();

            System.out.println("Dados carregados com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            FileWriter fileWriter = new FileWriter("dados1.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Produto produto : produtos) {
                bufferedWriter.write(produto.getNome() + "," + produto.getQuantidade() + "," + produto.getValor());
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
            fileWriter.close();

            System.out.println("Dados salvos com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    // RELATORIO DO USUARIO
    
  private void imprimirRelatorio() {
    JFileChooser fileChooser = new JFileChooser();

    // Define o nome padrão do arquivo como "relatorio.pdf"
    fileChooser.setSelectedFile(new File("relatorio.pdf"));

    // Define o filtro para exibir apenas arquivos PDF e TXT
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos PDF e TXT", "pdf", "txt");
    fileChooser.setFileFilter(filter);

    Object[] options = {"PDF", "Texto", "Cancelar"};

    // Mostra um diálogo de seleção para escolher o formato de saída
    int formatOption = JOptionPane.showOptionDialog(this, "Como deseja salvar seu Arquivo? ", "Formato do Relatório", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

    if (formatOption == 2) {
        // Usuário cancelou a operação
        return;
    }

    // Define a extensão de arquivo com base na escolha do formato de saída
    String fileExtension = (formatOption == 0) ? "pdf" : "txt";
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    int option = fileChooser.showSaveDialog(this);

    if (option == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        String filePath = file.getAbsolutePath();

        // Adiciona a extensão de arquivo correta, se necessário
        if (!filePath.toLowerCase().endsWith("." + fileExtension)) {
            filePath += "." + fileExtension;
        }

        try {
            if (fileExtension.equals("pdf")) {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));

                document.open();
                document.add(new Paragraph("------------------------------------------------------------"));
                document.add(new Paragraph("--    Relatório de Estoque Em Jardim MS   --"));
                document.add(new Paragraph("-------------------------------------------------------------"));

                // Obter a data atual
                Date dataAtual = new Date();

                // Definir o formato desejado para a data
                DateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

                // Converte a data para uma string formatada
                String dataFormatada = formatoData.format(dataAtual);

                // Adicionar a data formatada ao documento
                document.add(new Paragraph("Data: " + dataFormatada));

                // formatacao
                document.add(new Paragraph("---------------------------------------------------"));
                document.add(new Paragraph("--                   Seu Estoque                  --"));
                document.add(new Paragraph("----------------------------------------------------"));

                for (Produto produto : produtos) {
                    String info = "Nome: " + produto.getNome() + " | Quantidade: " + produto.getQuantidade()
                            + " | Valor: " + produto.getValor();
                    document.add(new Paragraph(info));
                }

                document.close();

                JOptionPane.showMessageDialog(this, "Relatório gerado com sucesso!");
            } else if (fileExtension.equals("txt")) {
                FileWriter writer = new FileWriter(filePath);

                writer.write("------------------------------------------------------------\n");
                writer.write("--    Relatório de Estoque Em Jardim MS   --\n");
                writer.write("-------------------------------------------------------------\n");

                // Obter a data atual
                Date dataAtual = new Date();

                // Definir o formato desejado para a data
                DateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

                // Converte a data para uma string formatada
                String dataFormatada = formatoData.format(dataAtual);

                // Adicionar a data formatada ao documento
                writer.write("Data: " + dataFormatada + "\n");

                // formatacao
                writer.write("---------------------------------------------------\n");
                writer.write("--                   Seu Estoque                  --\n");
                writer.write("----------------------------------------------------\n");

                for (Produto produto : produtos) {
                    String info = "Nome: " + produto.getNome() + " | Quantidade: " + produto.getQuantidade()
                            + " | Valor: " + produto.getValor() + "\n";
                    writer.write(info);
                }

                writer.close();

                JOptionPane.showMessageDialog(this, "Relatório gerado com sucesso!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar o relatório: " + e.getMessage());
        }
    }
}



private void pesquisarProduto() {
    String nome = JOptionPane.showInputDialog(this, "Digite o nome do produto a ser pesquisado:");
    if (nome != null && !nome.isEmpty()) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        RowSorter<? extends TableModel> previousSorter = table.getRowSorter(); // Armazena o RowSorter anterior
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + nome)); // Ignora case sensitive

        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado.");
            table.setRowSorter(previousSorter); // Restaura o RowSorter original
        } else {
            StringBuilder mensagem = new StringBuilder("Produtos encontrados:\n");
            for (int i = 0; i < table.getRowCount(); i++) {
                String nomeProduto = (String) table.getValueAt(i, 0);
                mensagem.append("- ").append(nomeProduto).append("\n");
            }
            JOptionPane.showMessageDialog(this, mensagem.toString());

            table.setRowSorter(previousSorter); // Restaura o RowSorter original
        }
    }
}



class Produto {
    private String nome;
    private int quantidade;
    private double valor;

    public Produto(String nome, int quantidade, double valor) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
}
