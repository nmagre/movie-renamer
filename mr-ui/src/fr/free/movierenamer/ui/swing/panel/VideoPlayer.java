/*
 * Copyright (C) 2015 duffy
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

import com.alee.global.StyleConstants;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.slider.WebSlider;
import com.alee.laf.slider.WebSliderUI;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.utils.GraphicsUtils;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.swing.renderer.Triangle;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;

/**
 *
 * @author duffy
 */
public class VideoPlayer extends WebPanel implements MediaPlayerEventListener {

  private static final long serialVersionUID = 1L;

  private final MovieRenamer mr;
  private final EmbeddedMediaPlayerComponent videoCanvas;
  private EmbeddedMediaPlayer player;
  private Timer t;
  private JPanel controlsPanel;
  private WebSlider timeline;
  private JButton play, back, forward;
  private JToggleButton loop, mute;
  private JSlider speed;
  private final String[] mediaOptions = {""};
  private boolean syncTimeline = false;
  private boolean looping = false;
  private SimpleDateFormat ms;
  private int jumpLength = 1000;
  private int loopLength = 6000;
  private javax.swing.Timer timer;
  String text = "";

  public VideoPlayer(MovieRenamer mr) {
    super();

    this.mr = mr;
    new NativeDiscovery().discover();

    videoCanvas = new EmbeddedMediaPlayerComponent() {
      @Override
      protected String[] onGetMediaPlayerFactoryExtraArgs() {
        return new String[]{"--no-osd"}; // Disables the display of the snapshot filename (amongst other things)
      }
    };
    videoCanvas.setVisible(true);
    FullScreenStrategy fullScreenStrategy = new DefaultFullScreenStrategy(mr);
    player = videoCanvas.getMediaPlayer();
    player.setFullScreenStrategy(fullScreenStrategy);
    setSize(400, 300);
    ms = new SimpleDateFormat("hh:mm:ss");
    timeline = new WebSlider(0, 100, 0);

    WebSliderUI wui = new WebSliderUI(timeline) {

      @Override
      public void paintTrack(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        final Object aa = GraphicsUtils.setupAntialias(g2d);
        thumbRect.height *= 2;
        trackRect.height *= 2;
        final Shape ss = new Triangle((float) trackRect.x, (float) thumbRect.y + thumbRect.height, (float) trackRect.width - 1, 0f, 1, thumbRect.height);

// Track shade
        if (slider.isEnabled()) {
          GraphicsUtils.drawShade(g2d, ss, slider.isFocusOwner() ? StyleConstants.fieldFocusColor : StyleConstants.shadeColor,
                  progressShadeWidth);
        }
// Track background
        g2d.setPaint(new GradientPaint(0, trackRect.y, trackBgTop, 0, trackRect.y + trackRect.height, trackBgBottom));

        g2d.fill(ss);

// Inner progress line
        if (true) {
// Progress shape

          float L = thumbRect.x + thumbRect.width / 2 - (trackRect.x + progressShadeWidth);
          float py = ((thumbRect.height - progressShadeWidth * 2) * (trackRect.width - 1 - L)) / (trackRect.width - 1);

          final Shape ps = new Triangle(trackRect.x + progressShadeWidth, thumbRect.y + thumbRect.height, thumbRect.x + thumbRect.width / 2 - (trackRect.x + progressShadeWidth), 0f, 1, thumbRect.height - py - progressShadeWidth);
// Progress shade

// Progress background
          final Rectangle bounds = ss.getBounds();
          g2d.setPaint(new GradientPaint(bounds.x + progressShadeWidth, 0, Color.GREEN, bounds.x + bounds.width - progressShadeWidth, 0, Color.RED)
          );

          g2d.fill(ps);
          g2d.draw(ps);
        }

        g2d.setPaint(slider.isEnabled()
                ? (rolloverDarkBorderOnly && !isDragging() ? getBorderColor() : StyleConstants.darkBorderColor)
                : StyleConstants.disabledBorderColor);
        g2d.draw(ss);

        GraphicsUtils.restoreAntialias(g2d, aa);
      }
    };
    timeline.setUI(wui);
    timeline.setDrawThumb(false);
    timeline.setPreferredHeight(140);

    timeline.setMajorTickSpacing(10);
    timeline.setMajorTickSpacing(5);
    timeline.setPaintTicks(false);
    timeline.removeMouseWheelListener(timeline.getMouseWheelListeners()[0]);
    timeline.addMouseWheelListener(new MouseWheelListener() {

      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        timeline.setValue(Math.min(Math.max(timeline.getMinimum(), timeline.getValue() - e.getWheelRotation()), timeline.getMaximum())
        );
      }
    });

    timeline.addMouseMotionListener(new MouseMotionListener() {

      @Override
      public void mouseDragged(MouseEvent me) {

      }

      @Override
      public void mouseMoved(final MouseEvent me) {
        if (player.isPlaying()) {

          if (timer != null) {
            timer.stop();
          }
          TooltipManager.hideAllTooltips();
          timer = new javax.swing.Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              long duration = player.getLength();
              WebSliderUI wm = (WebSliderUI) timeline.getUI();
              duration = (duration * wm.valueForXPosition(me.getX())) / 100;
              String s = String.format("%02d:%02d:%02d", //dont know why normal Java date utils doesn't format the time right
                      TimeUnit.MILLISECONDS.toHours(duration),
                      TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                      TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
              );

              TooltipManager.showOneTimeTooltip(timeline, new Point(me.getX(), me.getY()), s, TooltipWay.up);

              timer.stop();
            }
          });

          timer.start();
        }
      }
    });

    //TODO we need Icons instead
    play = new JButton("play");
    back = new JButton("<");
    forward = new JButton(">");
    loop = new JToggleButton("loop");
    mute = new JToggleButton("mute");
    speed = new JSlider(-200, 200, 0);
    speed.setMajorTickSpacing(100);
    speed.setPaintTicks(true);
    speed.setOrientation(Adjustable.VERTICAL);
    Hashtable labelTable = new Hashtable();
    labelTable.put(new Integer(0), new JLabel("1x"));
    labelTable.put(new Integer(-200), new JLabel("-2x"));
    labelTable.put(new Integer(200), new JLabel("2x"));
    speed.setLabelTable(labelTable);
    speed.setPaintLabels(true);

    // Layout
    setLayout(new BorderLayout());
    controlsPanel = new JPanel();
    JPanel mediacontrolsPanel = new JPanel();
    mediacontrolsPanel.setLayout(new BorderLayout());
    controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.PAGE_AXIS));
    add(videoCanvas, BorderLayout.CENTER);
    //fill screen panel
    add(mediacontrolsPanel, BorderLayout.SOUTH);
    add(speed, BorderLayout.EAST);
    controlsPanel.add(play);
    controlsPanel.add(back);
    controlsPanel.add(forward);
    controlsPanel.add(loop);
    controlsPanel.add(mute);
    mediacontrolsPanel.add(controlsPanel, BorderLayout.CENTER);
    mediacontrolsPanel.add(timeline, BorderLayout.SOUTH);
    loop.setSelected(false);
    mute.setSelected(false);

    setMargin(50);
    setOpaque(false);

    addListeners();

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleAtFixedRate(new Runnable() {
      //We have to do syncing in the main thread
      public void run() {
        SwingUtilities.invokeLater(new Runnable() {
          //here we update
          public void run() {
            if (player.isPlaying()) {
              long millis = player.getTime();
              String s = String.format("%02d:%02d:%02d", //dont know why normal Java date utils doesn't format the time right
                      TimeUnit.MILLISECONDS.toHours(millis),
                      TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                      TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
              );

              syncTimeline = true;
              timeline.setValue(Math.round(player.getPosition() * 100));

              syncTimeline = false;
            }
          }
        });
      }
    }, 0L, 1000L, TimeUnit.MILLISECONDS);

  }

  @Override
  public void setVisible(boolean aFlag) {
    super.setVisible(aFlag); //To change body of generated methods, choose Tools | Templates.
    //setPreferredSize(new Dimension(mr.getWidth() - 120, mr.getHeight() - 120));
  }

  public void play(String media) {
    player.prepareMedia(media);
    player.parseMedia();
    player.play();
  }

  //add UI functionality
  private void addListeners() {
    timeline.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (!syncTimeline) //only if user moves the slider by hand
        {
          if (!timeline.getValueIsAdjusting()) //and the slider is fixed
          {
            //recalc to 0.x percent value
            player.setPosition((float) timeline.getValue() / 100.0f);
          }
        }
      }
    });

    play.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {

        UIUtils.showNotification("Pause " + player.getMediaMeta().getTitle());

        if (player.isPlaying()) {
          player.pause();
        } else {
          player.play();
        }
      }
    });

    back.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        backward();
      }
    });

    forward.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        forward();
      }
    });

    loop.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        loop();
      }
    });

    mute.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        mute();
      }
    });

    speed.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent arg0) {
        if (!speed.getValueIsAdjusting() && (player.isPlaying())) {
          int perc = speed.getValue();
          float ratio = (float) (perc / 400f * 1.75);
          ratio = ratio + (9 / 8);
          player.setRate(ratio);
        }

      }
    });

  }

  public void loop() {
    if (looping) {
      t.cancel();
      looping = false;
    } else {
      final long resetpoint = player.getTime() - loopLength / 2;
      TimerTask ani = new TimerTask() {

        @Override
        public void run() {
          player.setTime(resetpoint);
        }
      };
      t = new Timer();
      t.schedule(ani, loopLength / 2, loopLength); //first run a half looptime till reset
      looping = true;
    }

  }

  protected void mute() {
    player.mute();

  }

  public void forward() {
    player.setTime(player.getTime() + jumpLength);
  }

  public void backward() {
    player.setTime(player.getTime() - jumpLength);
  }

  public void removeVideo() {
    if (player.isPlaying()) {
      player.stop();
    }
    player.release();
  }

  @Override
  public void mediaChanged(MediaPlayer mp, libvlc_media_t l, String string) {
    //
  }

  @Override
  public void opening(MediaPlayer mp) {
    UIUtils.showNotification("Opening " + mp.getMediaMeta().getTitle());
  }

  @Override
  public void buffering(MediaPlayer mp, float f) {
    UIUtils.showNotification("Buffering media");
  }

  @Override
  public void playing(MediaPlayer mp) {
    UIUtils.showNotification("Playing " + mp.getMediaMeta().getTitle());
  }

  @Override
  public void paused(final MediaPlayer mp) {
    if (mp.isPlaying()) {
      mp.pause();
    }
  }

  @Override
  public void stopped(MediaPlayer mp) {
    UIUtils.showNotification("Stopped " + mp.getMediaMeta().getTitle());
    if (mp.isPlaying()) {
      mp.stop();
    }
  }

  @Override
  public void forward(MediaPlayer mp) {
    mp.setTime(mp.getTime() + jumpLength);
  }

  @Override
  public void backward(MediaPlayer mp) {
    mp.setTime(mp.getTime() - jumpLength);
  }

  @Override
  public void finished(MediaPlayer mp) {
    UIUtils.showNotification("Media finished");
  }

  @Override
  public void timeChanged(MediaPlayer mp, long l) {
    //
  }

  @Override
  public void positionChanged(MediaPlayer mp, float f) {
    //
  }

  @Override
  public void seekableChanged(MediaPlayer mp, int i) {

  }

  @Override
  public void pausableChanged(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void titleChanged(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void snapshotTaken(MediaPlayer mp, String string) {
    //
  }

  @Override
  public void lengthChanged(MediaPlayer mp, long l) {
    //
  }

  @Override
  public void videoOutput(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void scrambledChanged(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void elementaryStreamAdded(MediaPlayer mp, int i, int i1) {
    //
  }

  @Override
  public void elementaryStreamDeleted(MediaPlayer mp, int i, int i1) {
    //
  }

  @Override
  public void elementaryStreamSelected(MediaPlayer mp, int i, int i1) {
    //
  }

  @Override
  public void error(MediaPlayer mp) {
    System.err.println("ERROR");
  }

  @Override
  public void mediaMetaChanged(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void mediaSubItemAdded(MediaPlayer mp, libvlc_media_t l) {
    //
  }

  @Override
  public void mediaDurationChanged(MediaPlayer mp, long l) {
    //
  }

  @Override
  public void mediaParsedChanged(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void mediaFreed(MediaPlayer mp) {
    //
  }

  @Override
  public void mediaStateChanged(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void mediaSubItemTreeAdded(MediaPlayer mp, libvlc_media_t l) {
    //
  }

  @Override
  public void newMedia(MediaPlayer mp) {
    //
  }

  @Override
  public void subItemPlayed(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void subItemFinished(MediaPlayer mp, int i) {
    //
  }

  @Override
  public void endOfSubItems(MediaPlayer mp) {
    //
  }

}
