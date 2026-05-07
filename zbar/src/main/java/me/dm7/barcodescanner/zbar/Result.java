package me.dm7.barcodescanner.zbar;

public class Result {
    private String contents;
    private BarcodeFormat barcodeFormat;

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setBarcodeFormat(BarcodeFormat format) {
        this.barcodeFormat = format;
    }

    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    public String getContents() {
        return contents;
    }
}
