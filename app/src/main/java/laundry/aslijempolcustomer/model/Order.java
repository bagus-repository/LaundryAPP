package laundry.aslijempolcustomer.model;

import java.util.List;

/**
 * Created by Bagus on 25/07/2018.
 */
public class Order {
    private String tanggalOrder;
    private String jamOrder;
    private String addressOrder;
    private String[] pakaianOrder;
    private Integer[] jmlpakaianOrder;
    private String tipeService;
    private String noteOrder;

    public String getNoteOrder() {
        return this.noteOrder;
    }

    public void setNoteOrder(String noteOrder) {
        this.noteOrder = noteOrder;
    }

    public void setTanggalOrder(String tanggalOrder){
        this.tanggalOrder = tanggalOrder;
    }

    public String getTanggalOrder() {
        return this.tanggalOrder;
    }

    public void setJamOrder(String jamOrder){
        this.jamOrder = jamOrder;
    }

    public String getJamOrder() {
        return this.jamOrder;
    }

    public void setAddressOrder(String addressOrder){
        this.addressOrder = addressOrder;
    }

    public String getAddressOrder() {
        return this.addressOrder;
    }

    public void setPakaianOrder(String[] pakaianOrder){
        this.pakaianOrder = pakaianOrder;
    }

    public String[] getPakaianOrder() {
        return this.pakaianOrder;
    }

    public void setJmlpakaianOrder(Integer[] jmlpakaianOrder){
        this.jmlpakaianOrder = jmlpakaianOrder;
    }

    public void setJmlpakaianOrderByPos(Integer pos, Integer value){
        this.jmlpakaianOrder[pos] = value;
    }

    public Integer[] getJmlpakaianOrder() {
        return this.jmlpakaianOrder;
    }

    public void setTipeService(String tipeService) {
        this.tipeService = tipeService;
    }

    public String getTipeService() {
        return tipeService;
    }
}
