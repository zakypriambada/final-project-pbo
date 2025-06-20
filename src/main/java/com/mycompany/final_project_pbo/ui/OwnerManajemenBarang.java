/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.final_project_pbo.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.mycompany.final_project_pbo.models.Category;
import com.mycompany.final_project_pbo.models.LogActivity;
import com.mycompany.final_project_pbo.models.Product;
import com.mycompany.final_project_pbo.models.StockTransaction;
import com.mycompany.final_project_pbo.models.TransactionType;
import com.mycompany.final_project_pbo.models.User;
import com.mycompany.final_project_pbo.repositories.CategoryRepository;
import com.mycompany.final_project_pbo.repositories.LogActivityRepository;
import com.mycompany.final_project_pbo.repositories.ProductRepository;
import com.mycompany.final_project_pbo.repositories.StockTransactionRepository;
import com.mycompany.final_project_pbo.repositories.UserRepository;
import com.mycompany.final_project_pbo.utils.Response;
import com.mycompany.final_project_pbo.utils.SessionManager;

/**
 *
 * @author Achmad Fathoni
 */
public class OwnerManajemenBarang extends javax.swing.JPanel {

    /**
     * Creates new form OwnerManajemenBarang
     */
    public OwnerManajemenBarang() {
        initComponents();
        initializeComponents();
    }

    private final User currentUser = SessionManager.getInstance().getCurrentUser();
    private final ProductRepository productRepository = new ProductRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final StockTransactionRepository stockTransactionRepository = new StockTransactionRepository();
    private final UserRepository userRepository = new UserRepository();

    private void initializeComponents() {
        initSortItem();
        initSortRiwayatAktivitas();
        initSearchBarang();

        populateTableBarang();
        populateTableRiwayatAktivitas();
    }

    private void initSearchBarang() {
        SearchBarang.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = SearchBarang.getText().trim();
                TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) TabelManajemenBarang1
                        .getRowSorter();

                if (sorter == null) {
                    sorter = new TableRowSorter<>((DefaultTableModel) TabelManajemenBarang1.getModel());
                    TabelManajemenBarang1.setRowSorter(sorter);
                }

                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        });
    }

    private void initSortItem() {
        String[] sortOptions = { "", "IDBarang", "Nama", "Barcode", "Kategori", "HargaBeli", "HargaJual", "Stok" };
        SortItem.setModel(new DefaultComboBoxModel<>(sortOptions));
        SortItem.setSelectedIndex(0);

        SortItem.addActionListener(evt -> {
            String selectedSort = (String) SortItem.getSelectedItem();
            if (selectedSort != null) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(
                        (DefaultTableModel) TabelManajemenBarang1.getModel());
                int columnIndex = SortItem.getSelectedIndex();
                sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING)));
                TabelManajemenBarang1.setRowSorter(sorter);
            }
        });
    }

    private void initSortRiwayatAktivitas() {
        String[] sortOptions = { "", "Tanggal & Waktu", "Aktivitas", "Jenis Aktivitas", "Jumlah", "Barang", "Oleh" };
        SortRiwayatAktivitas.setModel(new DefaultComboBoxModel<>(sortOptions));
        SortRiwayatAktivitas.setSelectedIndex(0);

        SortRiwayatAktivitas.addActionListener(evt -> {
            String selectedSort = (String) SortRiwayatAktivitas.getSelectedItem();
            if (selectedSort != null) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(
                        (DefaultTableModel) TabelRiwayatAktivitas.getModel());
                int columnIndex = SortRiwayatAktivitas.getSelectedIndex();
                sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING)));
                TabelRiwayatAktivitas.setRowSorter(sorter);
            }
        });
    }

    private void populateTableBarang() {
        String[] columnNames = { "IDBarang", "Nama", "Barcode", "Kategori", "HargaBeli", "HargaJual", "Stok" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Response<ArrayList<Product>> response = productRepository.findAll(currentUser.getId());
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data barang yang ditemukan.");
            return;
        }

        for (Product product : response.getData()) {
            Response<Category> categoryResponse = categoryRepository.findById(product.getCategoryId(),
                    currentUser.getId());
            String categoryName = categoryResponse.isSuccess()
                    ? categoryResponse.getData().getName()
                    : "Tidak Ditemukan";

            Object[] row = {
                    product.getId(),
                    product.getName(),
                    product.getBarcode(),
                    categoryName,
                    product.getPurchasePrice(),
                    product.getSellingPrice(),
                    product.getStock()
            };
            model.addRow(row);
        }

        TabelManajemenBarang1.setModel(model);
        TabelManajemenBarang1.setRowSorter(new TableRowSorter<>(model));
    }

    private void populateTableRiwayatAktivitas() {
        String[] columnNames = { "Tanggal & Waktu", "Aktivitas", "Jenis Aktivitas", "Jumlah", "Barang", "Oleh" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Response<ArrayList<StockTransaction>> response = stockTransactionRepository.findAll(currentUser.getId());
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Tidak ada riwayat aktivitas yang ditemukan.");
            return;
        }

        for (StockTransaction transaction : response.getData()) {
            String typeActivity = switch (transaction.getTransactionType()) {
                case IN -> "Barang Masuk";
                case OUT -> "Barang Keluar";
                default -> "Edit";
            };

            Response<Product> productResponse = productRepository.findById(transaction.getProductId(),
                    currentUser.getId());
            String productName = productResponse.isSuccess()
                    ? productResponse.getData().getName()
                    : "Tidak Ditemukan";

            Response<User> userResponse = userRepository.findById(transaction.getUserId(), currentUser.getId());
            String userName = userResponse.isSuccess()
                    ? userResponse.getData().getUsername()
                    : "Tidak Ditemukan";

            Object[] row = {
                    transaction.getCreatedAt(),
                    transaction.getDescription(),
                    typeActivity,
                    transaction.getQuantity(),
                    productName,
                    userName
            };
            model.addRow(row);
        }

        TabelRiwayatAktivitas.setModel(model);
        TabelRiwayatAktivitas.setRowSorter(new TableRowSorter<>(model));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        SortItem = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        SortRiwayatAktivitas = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        TabelRiwayatAktivitas = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        SearchBarang = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        TabelManajemenBarang1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        SortItem1 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        SortRiwayatAktivitas1 = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        TabelRiwayatAktivitas1 = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        SearchBarang1 = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        TabelManajemenBarang2 = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(890, 583));
        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(93, 173, 226));
        jLabel1.setText("Data Barang Toko Berkah Abadi");

        jLabel2.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel2.setText("Urutkan Berdasarkan: ");

        SortItem.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        SortItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SortItemActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(93, 173, 226));
        jLabel8.setText("Riwayat Aktivitas");

        jLabel9.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel9.setText("Urutkan Berdasarkan: ");

        SortRiwayatAktivitas.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        SortRiwayatAktivitas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SortRiwayatAktivitasActionPerformed(evt);
            }
        });

        TabelRiwayatAktivitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tanggal&Waktu", "Nama", "Tambah/Hapus/Edit", "Jumlah"
            }
        ));
        jScrollPane2.setViewportView(TabelRiwayatAktivitas);

        jLabel10.setFont(new java.awt.Font("Tw Cen MT", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(93, 173, 226));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Menu Barang");

        SearchBarang.setFont(new java.awt.Font("Tw Cen MT", 1, 14)); // NOI18N

        TabelManajemenBarang1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "IDBarang", "Nama", "Barcode", "Kategori", "HargaBeli", "HargaJual", "Stok"
            }
        ));
        jScrollPane3.setViewportView(TabelManajemenBarang1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SortRiwayatAktivitas, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(SortItem, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 214, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(SearchBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(16, 16, 16))))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(15, 15, 15)
                        .addComponent(SearchBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SortItem)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(SortRiwayatAktivitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        add(jPanel1, "card2");

        jPanel2.setPreferredSize(new java.awt.Dimension(890, 583));
        jPanel2.setLayout(new java.awt.CardLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(93, 173, 226));
        jLabel3.setText("Data Barang Toko Berkah Abadi");

        jLabel4.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel4.setText("Urutkan Berdasarkan: ");

        SortItem1.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        SortItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SortItem1ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(93, 173, 226));
        jLabel11.setText("Riwayat Aktivitas");

        jLabel12.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel12.setText("Urutkan Berdasarkan: ");

        SortRiwayatAktivitas1.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        SortRiwayatAktivitas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SortRiwayatAktivitas1ActionPerformed(evt);
            }
        });

        TabelRiwayatAktivitas1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tanggal&Waktu", "Nama", "Tambah/Hapus/Edit", "Jumlah"
            }
        ));
        jScrollPane4.setViewportView(TabelRiwayatAktivitas1);

        jLabel13.setFont(new java.awt.Font("Tw Cen MT", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(93, 173, 226));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Menu Barang");

        SearchBarang1.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N

        TabelManajemenBarang2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "IDBarang", "Nama", "Barcode", "Kategori", "HargaBeli", "HargaJual", "Stok"
            }
        ));
        jScrollPane5.setViewportView(TabelManajemenBarang2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SortRiwayatAktivitas1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(SortItem1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(SearchBarang1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel13))
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(SortItem1)
                        .addComponent(SearchBarang1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel11)
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(SortRiwayatAktivitas1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel3, "card2");

        add(jPanel2, "card3");
    }// </editor-fold>//GEN-END:initComponents

    private void SortItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SortItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SortItem1ActionPerformed

    private void SortRiwayatAktivitas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SortRiwayatAktivitas1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SortRiwayatAktivitas1ActionPerformed

    private void SortRiwayatAktivitasActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_SortRiwayatAktivitasActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_SortRiwayatAktivitasActionPerformed

    private void SortItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_SortItemActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_SortItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField SearchBarang;
    private javax.swing.JTextField SearchBarang1;
    private javax.swing.JComboBox<String> SortItem;
    private javax.swing.JComboBox<String> SortItem1;
    private javax.swing.JComboBox<String> SortRiwayatAktivitas;
    private javax.swing.JComboBox<String> SortRiwayatAktivitas1;
    private javax.swing.JTable TabelManajemenBarang1;
    private javax.swing.JTable TabelManajemenBarang2;
    private javax.swing.JTable TabelRiwayatAktivitas;
    private javax.swing.JTable TabelRiwayatAktivitas1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    // End of variables declaration//GEN-END:variables
}
