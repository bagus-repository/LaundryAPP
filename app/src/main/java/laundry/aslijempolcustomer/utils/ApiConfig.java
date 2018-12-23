package laundry.aslijempolcustomer.utils;

/**
 * Created by Bagus on 24/07/2018.
 */
public class ApiConfig {
    //Host
    public static final String HOST = "aslijempol.ardata.co.id";
    public static final String PHONE = "6281909111991";
    //Server URL
    public static final String URL = "http://"+HOST+"/api/";
    //url faq
    public static final String URL_FAQ = "http://"+HOST+"/faq";
    //Register URL (POST)
    public static final String URL_REGISTER = URL+"pelanggan/add";
    //Login URL (POST)
    public static final String URL_LOGIN = URL+"pelanggan/login";
    //Cek App Update URL (POST)
    public static final String URL_CEK_APP_UPDATE = URL+"app/update";
    //Ambil image slider (POST)
    public static final String URL_GET_IMAGE_SLIDER = URL+"app/getimageslider";
    //cust addres post
    public static final String URL_GET_CUST_ADDRS = URL+"pelanggan/getaddress";
    //get order items post
    public final static String URL_GET_ORDER_ITEMS = URL+"app/getorderitems";
    //get order service post
    public final static String URL_GET_SERVICE = URL+"app/getservices";
    //get voucher post
    public final static String URL_GET_VOUCHER = URL+"app/voucher";
    //post order data
    public final static String URL_SUBMIT_ORDER = URL+"app/submitorder";
    //get cust order on procc
    public final static String URL_ORDER = URL+"pelanggan/getorder";
    //change pass
    public final static String URL_CHANGE_PASS = URL+"pelanggan/changepass";
    //getlist addr
    public static final String URL_GET_ADDRS = URL+"pelanggan/getaddresslist";
    //hapus alamat
    public static final String URL_DEL_ADDR = URL+"pelanggan/deleteaddress";
    //add alamat
    public static final String URL_ADD_ADDR = URL+"pelanggan/addaddress";
    //URL Invoice
    public static final String URL_INVOICE = "http://"+HOST+"/invoice";
    //addr by id
    public static final String URL_GET_ADDRS_BY_ID = URL+"pelanggan/getaddressbyid";
    //url about
    public static final String URL_ABOUT = "http://"+HOST+"/about";
    public static final String EMAIL = "cs@aslijempol.ardata.co.id";
    //update os_player_id
    public static final String URL_UPDATE_OS_PLAYER_ID = URL+"pelanggan/update_os_player_id";
    //get promo and news
    public final static String URL_GET_PROMO_NEWS = URL+"app/get_promo_news";
    //check email exist
    public static final String URL_IS_EMAIL_REGISTERED = URL+"pelanggan/is_registered";
    //reset password
    public static final String URL_RESET_PASSWORD = URL+"pelanggan/reset_password";
    //get order detail
    public static final String URL_GET_ORDER_DETAIL = URL+"pelanggan/order_detail";
    public static final String URL_GET_PERFUME = URL+"app/getparfumes";

    //get point
    public static final String URL_GET_POINT_SUM = URL+"pelanggan/get_point_sum";
    //get point
    public static final String URL_DASH_POIN_DATA = URL+"pelanggan/dash_poin_data";
    //get point
    public static final String URL_RIWAYAT_POIN = URL+"pelanggan/riwayat_poin";
    //get point
    public static final String URL_REDEEM_POIN = URL+"pelanggan/redeem_poin";

    public static final String URL_KUPON_KU = URL+"pelanggan/list_kupon";
}
