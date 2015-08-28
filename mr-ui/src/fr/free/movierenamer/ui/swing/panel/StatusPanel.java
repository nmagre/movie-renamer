/*
 * Movie_Renamer
 * Copyright (C) 2015 Nicolas Magré
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.ui.swing.panel;

import com.alee.laf.panel.WebPanel;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.IEventListener;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.swing.TaskPopup;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.worker.AbstractWorker;
import fr.free.movierenamer.ui.worker.impl.ImageWorker;
import java.util.LinkedHashMap;

/**
 *
 * @author Nicolas Magré
 */
public class StatusPanel extends WebPanel implements IEventListener {

    private final TaskPopup taskPopup;
    private final LinkedHashMap<AbstractWorker<?, ?>, TaskPanel> task;
    private AbstractWorker<?, ?> focusWorker = null;

    /**
     * Creates new form StatusPanel
     */
    public StatusPanel() {
        task = new LinkedHashMap<>();
        initComponents();
        moreBtn.setInnerShadeWidth(0);
        moreBtn.setRolloverDecoratedOnly(true);

        moreBtn.setFocusable(true);
        taskPopup = new TaskPopup(moreBtn);
        UIEvent.addEventListener(StatusPanel.class, this);
    }

//      String tasktitle = mediaFile.getName();
//      if (tasktitle.length() > 30) {
//          tasktitle = tasktitle.substring(0, 27) + "...";
//      }
    @Override
    public void UIEventHandler(UIEvent.Event event, IEventInfo eventInfo, Object object, Object newObject) {

        switch (event) {
            case WORKER_STARTED:
                AbstractWorker<?, ?> worker = (AbstractWorker<?, ?>) eventInfo.getEventObject();
                if (worker.isEventEnable()) {
                    TaskPanel tpanel = new TaskPanel(worker.getDisplayName());
                    task.put(worker, tpanel);
                    taskPopup.addTaskPanel(tpanel);
                    moreBtn.setEnabled(task.size() > 1);
                    moreBtn.setVisible(true);
                    if (focusWorker == null) {
                        focusWorker = worker;
                        statusLbl.setIcon(ImageUtils.LOAD_8);
                        statusLbl.setText(worker.getDisplayName());
                        workerProgress.setVisible(true);
                    }
                }
                break;
            case WORKER_RUNNING:
            case WORKER_PROGRESS:
                worker = (AbstractWorker<?, ?>) eventInfo.getEventObject();                
                TaskPanel tpanel = task.get(worker);
                if (tpanel != null) {
                    workerProgress.setVisible(true);
                    int progress = worker.getProgress();
                    tpanel.setProgress(progress);
                    if (focusWorker.equals(worker)) {
                        statusLbl.setIcon(ImageUtils.LOAD_8);
                        statusLbl.setText(worker.getDisplayName());
                        workerProgress.setIndeterminate(progress < 0);
                        workerProgress.setStringPainted(progress >= 0);
                        workerProgress.setValue(worker.getProgress());
                    }
                } else if(!worker.isDone()) {
                    System.out.println("ERROR UNKONW PROGRESS");
                    System.out.println(worker.getWorkerId());
                    System.out.println(worker.getDisplayName());
                    System.out.println(worker.getProgress());
                    System.out.println(worker.getState());
                    System.out.println(worker.toString());
                    System.out.println(worker.isEventEnable());
                    System.out.println(worker.isCancelled());
                    System.exit(-1);
                }
                break;
            case WORKER_DONE_ERROR:
            case WORKER_DONE:
            case WORKER_CANCEL:
                worker = (AbstractWorker<?, ?>) eventInfo.getEventObject();
                tpanel = task.get(worker);
                if (tpanel != null) {
                    task.remove(worker);
                    taskPopup.removeTaskPanel(tpanel);
                }

                if (!task.isEmpty() && focusWorker.equals(worker)) {
                    AbstractWorker<?, ?>[] workers = task.keySet().toArray(new AbstractWorker<?, ?>[0]);
                    focusWorker = workers[workers.length - 1];
                    int progress = focusWorker.getProgress();
                    statusLbl.setIcon(ImageUtils.LOAD_8);
                    statusLbl.setText(worker.getDisplayName());
                    workerProgress.setIndeterminate(progress < 0);
                    workerProgress.setStringPainted(progress >= 0);
                    workerProgress.setValue(worker.getProgress());
                }

                moreBtn.setEnabled(task.size() > 1);
                if (task.size() < 2) {
                    taskPopup.hidePopup();
                }

                if (task.isEmpty()) {
                    done();
                }
                break;
            case WORKER_ALL_DONE:
                done();
                break;
        }
    }

    private void done() {
        statusLbl.setText("");
        statusLbl.setIcon(null);
        workerProgress.setIndeterminate(true);
        workerProgress.setStringPainted(false);
        workerProgress.setValue(0);
        workerProgress.setVisible(false);
        moreBtn.setEnabled(false);
        moreBtn.setVisible(false);
        taskPopup.hidePopup();
        taskPopup.clearTask();
        task.clear();
        focusWorker = null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        moreBtn = new com.alee.laf.button.WebButton();
        workerProgress = new com.alee.laf.progressbar.WebProgressBar();
        statusLbl = new com.alee.laf.label.WebLabel();
        webLabel2 = new com.alee.laf.label.WebLabel();

        setName(""); // NOI18N

        moreBtn.setText("⯅");
        moreBtn.setAlignmentY(0.0F);
        moreBtn.setFocusable(false);
        moreBtn.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
        moreBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreBtnActionPerformed(evt);
            }
        });

        workerProgress.setFont(new java.awt.Font("Dialog", 3, 8)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(webLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 367, Short.MAX_VALUE)
                .addComponent(statusLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(workerProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moreBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(moreBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(workerProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(statusLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(webLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void moreBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreBtnActionPerformed
        //taskPopup.showPopup(moreBtn);
    }//GEN-LAST:event_moreBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.alee.laf.button.WebButton moreBtn;
    private com.alee.laf.label.WebLabel statusLbl;
    private com.alee.laf.label.WebLabel webLabel2;
    private com.alee.laf.progressbar.WebProgressBar workerProgress;
    // End of variables declaration//GEN-END:variables
}
