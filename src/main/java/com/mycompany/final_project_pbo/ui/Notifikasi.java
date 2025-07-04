/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.final_project_pbo.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.mycompany.final_project_pbo.models.LogActivity;
import com.mycompany.final_project_pbo.models.Notification;
import com.mycompany.final_project_pbo.models.NotificationType;
import com.mycompany.final_project_pbo.models.User;
import com.mycompany.final_project_pbo.repositories.LogActivityRepository;
import com.mycompany.final_project_pbo.repositories.NotificationRepository;
import com.mycompany.final_project_pbo.utils.Response;
import com.mycompany.final_project_pbo.utils.SessionManager;

/**
 *
 * @author zakyp
 */
public class Notifikasi extends javax.swing.JPanel {

        /**
         * Creates new form Notifikasi
         */
        public Notifikasi() {
                initComponents();
                initializeComponents();
        }

        private void initializeComponents() {
                populateTableBarangdanStock();
                populateTableKeuangan();
                populateTableActivitasSistem();
        }

        User currentUser = SessionManager.getInstance().getCurrentUser();
        LogActivityRepository logActivityRepository = new LogActivityRepository();
        NotificationRepository notificationRepository = new NotificationRepository();

        private void populateNotificationTable(DefaultTableModel model, NotificationType type, JTable table,
                        String noDataMsg) {
                // Ambil notifikasi dari repository
                Response<ArrayList<Notification>> response = notificationRepository.findAll(null);
                System.out.println("Response: " + response);

                if (response.isSuccess() && response.getData() != null && !response.getData().isEmpty()) {
                        int no = 1;
                        for (Notification notification : response.getData()) {
                                if (notification.getType() == null || !notification.getType().equals(type)) {
                                        continue; // Skip notifications that are not of the desired type
                                }
                                // Proteksi jika message null
                                String message = notification.getMessage() != null ? notification.getMessage()
                                                : "(Pesan kosong)";
                                model.addRow(new Object[] { no++, message });
                        }
                        // Jika tidak ada notifikasi dengan tipe yang diinginkan
                        if (model.getRowCount() == 0) {
                                model.addRow(new Object[] { "1", noDataMsg });
                        }
                } else {
                        model.addRow(new Object[] { "1", noDataMsg });
                }
                table.setModel(model);
        }

        private void populateTableKeuangan() {
                String[] columnNames = { "No", "Keuangan" };
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
                };
                populateNotificationTable(model, NotificationType.DEBT, TableKeuangan,
                                "Tidak ada notifikasi keuangan saat ini.");
        }

        private void populateTableBarangdanStock() {
                String[] columnNames = { "No", "Barang & Stock" };
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
                };
                populateNotificationTable(model, NotificationType.PRODUCT, TableBarangdanStock,
                                "Tidak ada notifikasi stok saat ini.");
        }

        private void populateTableActivitasSistem() {
                String[] columnNames = { "No", "Aktivitas Sistem" };
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
                };

                Response<ArrayList<LogActivity>> response = logActivityRepository.findAll();
                int limit = 10; // Change this to set how many recent activities you want to show

                if (response.isSuccess() && response.getData() != null && !response.getData().isEmpty()) {
                        ArrayList<LogActivity> allActivities = response.getData();

                        // Show the last 'limit' entries (assuming latest activities are at the end)
                        int startIndex = Math.max(0, allActivities.size() - limit);
                        List<LogActivity> latestActivities = allActivities.subList(startIndex, allActivities.size());

                        int no = 1;
                        for (LogActivity logActivity : latestActivities) {
                                String[] rowData = { String.valueOf(no++), logActivity.getAction() };
                                model.addRow(rowData);
                        }

                } else {
                        String[] rowData = { "1", "Tidak ada aktivitas sistem saat ini." };
                        model.addRow(rowData);
                }

                TableAktivitasSistem.setModel(model);

        }

        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jScrollPane1 = new javax.swing.JScrollPane();
                TableBarangdanStock = new javax.swing.JTable();
                jScrollPane2 = new javax.swing.JScrollPane();
                TableKeuangan = new javax.swing.JTable();
                jScrollPane3 = new javax.swing.JScrollPane();
                TableAktivitasSistem = new javax.swing.JTable();
                jPanel1 = new javax.swing.JPanel();
                jLabel2 = new javax.swing.JLabel();

                setPreferredSize(new java.awt.Dimension(890, 590));

                TableBarangdanStock.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {
                                                { "1", "Stok habis : Gula pasir tersisa 0 pcs." },
                                                { null, null },
                                                { null, null },
                                                { null, null },
                                                { null, null }
                                },
                                new String[] {
                                                "No", "Barang & Stock"
                                }));
                TableBarangdanStock.setGridColor(new java.awt.Color(204, 204, 204));
                jScrollPane1.setViewportView(TableBarangdanStock);
                if (TableBarangdanStock.getColumnModel().getColumnCount() > 0) {
                        TableBarangdanStock.getColumnModel().getColumn(0).setMaxWidth(50);
                }

                jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));

                TableKeuangan.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {
                                                { "1", "Piutang jatuh tempo : Rp 500.000 dari pelanggan Andi." },
                                                { null, null },
                                                { null, null },
                                                { null, null },
                                                { null, null }
                                },
                                new String[] {
                                                "No", "Keuangan"
                                }));
                TableKeuangan.setGridColor(new java.awt.Color(204, 204, 204));
                TableKeuangan.setSelectionBackground(new java.awt.Color(204, 204, 204));
                jScrollPane2.setViewportView(TableKeuangan);
                if (TableKeuangan.getColumnModel().getColumnCount() > 0) {
                        TableKeuangan.getColumnModel().getColumn(0).setMaxWidth(50);
                }

                TableAktivitasSistem.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {
                                                { "1", "Transaksi Baru: Rp 250.000 via QRIS." },
                                                { null, "" },
                                                { null, null },
                                                { null, null },
                                                { null, null }
                                },
                                new String[] {
                                                "No", "Aktivitas Sistem"
                                }));
                jScrollPane3.setViewportView(TableAktivitasSistem);
                if (TableAktivitasSistem.getColumnModel().getColumnCount() > 0) {
                        TableAktivitasSistem.getColumnModel().getColumn(0).setMaxWidth(50);
                }

                jPanel1.setBackground(new java.awt.Color(75, 139, 213));

                jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
                jLabel2.setForeground(new java.awt.Color(255, 255, 255));
                jLabel2.setText("Notifikasi");

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(17, 17, 17)
                                                                .addComponent(jLabel2)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));
                jPanel1Layout.setVerticalGroup(
                                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(jLabel2)
                                                                .addContainerGap(16, Short.MAX_VALUE)));

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jScrollPane1)
                                                                                .addComponent(jScrollPane2,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                705,
                                                                                                Short.MAX_VALUE)
                                                                                .addComponent(jScrollPane3,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                705,
                                                                                                Short.MAX_VALUE))
                                                                .addContainerGap())
                                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jPanel1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jScrollPane1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                94,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jScrollPane2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                94,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jScrollPane3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                94,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(1435, Short.MAX_VALUE)));
        }// </editor-fold>//GEN-END:initComponents

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JTable TableAktivitasSistem;
        private javax.swing.JTable TableBarangdanStock;
        private javax.swing.JTable TableKeuangan;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JScrollPane jScrollPane2;
        private javax.swing.JScrollPane jScrollPane3;
        // End of variables declaration//GEN-END:variables
}
