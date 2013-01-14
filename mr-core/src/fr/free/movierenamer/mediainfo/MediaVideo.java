/*
 * movie-renamer-core
 * Copyright (C) 2012 Nicolas Magré
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
package fr.free.movierenamer.mediainfo;

/**
 * Class MediaVideo
 *
 * @author Nicolas Magré
 */
public class MediaVideo {

  private String codec;
  private Double frameRate;
  private String scanType;
  private Long frameCount;
  private int height;
  private int width;
  private Float aspectRatio;

  public MediaVideo() {
    codec = "";
    frameRate = 0.0;
    scanType = "";
    frameCount = 0L;
    height = 0;
    width = 0;
    aspectRatio = 0.0F;
  }

  public String getCodec() {
    return codec;
  }

  public void setCodec(String videoCodec) {
    this.codec = videoCodec;
  }

  public Double getFrameRate() {
    return frameRate;
  }

  public void setFrameRate(Double frameRate) {
    this.frameRate = frameRate;
  }

  public String getScanType() {
    return scanType;
  }

  public void setScanType(String scanType) {
    this.scanType = scanType;
  }

  public Long getFrameCount() {
    return frameCount;
  }

  public void setFrameCount(Long frameCount) {
    this.frameCount = frameCount;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public Float getAspectRatio() {
    return aspectRatio;
  }

  public void setAspectRatio(Float aspectRatio) {
    this.aspectRatio = aspectRatio;
  }

  public String getVideoResolution() {
    return width + "x" + height;
  }

  public String getVideoDefinition() {
    return height >= 720 ? "HD":"SD";
  }
}
