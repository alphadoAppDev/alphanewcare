
package kr.co.alphacare.pets;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.alphacare.R;

public final class Dog {

    final String TAG = "Dog";


    public static List<Map<String, Object>> GetDogKind(Context context, int type) {
        List<Map<String, Object>> dialogItemList;
        int[] image = {R.drawable.pet, R.drawable.pet};
        String[] text = {context.getResources().getString(R.string.dog_golden_retriever), context.getResources().getString(R.string.dog_greyhound)};
        ArrayList<String> kind = new ArrayList<String>();
        String[] szValue;

        int length = 155;

        dialogItemList = new ArrayList<>();

        for(int i = 1; i < length; i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            szValue = context.getResources().getString(getKey(type, i)).split(",");

            itemMap.put("kind", szValue[0]);
            itemMap.put("name", szValue[1]);

            dialogItemList.add(itemMap);

            Log.e("TTTTTTTT", "dog" + i + ": kind: " +szValue[0] + ", name: " + szValue[1]);
        }
        Log.e("kkkkkkkkkk", "count: " + dialogItemList.size());

        return dialogItemList;
    }

    private  static int getKey(int type, int index) {
        int key;
        if (type == 1) key = getDogKey(index);
        else  key = getCatKey(index);

        return key;
    }

    private static int getDogKey(int index) {
        int key;

        switch (index) {
            case 1:	 key = R.string.dog001;	break;
            case 2:	 key = R.string.dog002;	break;
            case 3:	 key = R.string.dog003;	break;
            case 4:	 key = R.string.dog004;	break;
            case 5:	 key = R.string.dog005;	break;
            case 6:	 key = R.string.dog006;	break;
            case 7:	 key = R.string.dog007;	break;
            case 8:	 key = R.string.dog008;	break;
            case 9:	 key = R.string.dog009;	break;
            case 10:	 key = R.string.dog010;	break;
            case 11:	 key = R.string.dog011;	break;
            case 12:	 key = R.string.dog012;	break;
            case 13:	 key = R.string.dog013;	break;
            case 14:	 key = R.string.dog014;	break;
            case 15:	 key = R.string.dog015;	break;
            case 16:	 key = R.string.dog016;	break;
            case 17:	 key = R.string.dog017;	break;
            case 18:	 key = R.string.dog018;	break;
            case 19:	 key = R.string.dog019;	break;
            case 20:	 key = R.string.dog020;	break;
            case 21:	 key = R.string.dog021;	break;
            case 22:	 key = R.string.dog022;	break;
            case 23:	 key = R.string.dog023;	break;
            case 24:	 key = R.string.dog024;	break;
            case 25:	 key = R.string.dog025;	break;
            case 26:	 key = R.string.dog026;	break;
            case 27:	 key = R.string.dog027;	break;
            case 28:	 key = R.string.dog028;	break;
            case 29:	 key = R.string.dog029;	break;
            case 30:	 key = R.string.dog030;	break;
            case 31:	 key = R.string.dog031;	break;
            case 32:	 key = R.string.dog032;	break;
            case 33:	 key = R.string.dog033;	break;
            case 34:	 key = R.string.dog034;	break;
            case 35:	 key = R.string.dog035;	break;
            case 36:	 key = R.string.dog036;	break;
            case 37:	 key = R.string.dog037;	break;
            case 38:	 key = R.string.dog038;	break;
            case 39:	 key = R.string.dog039;	break;
            case 40:	 key = R.string.dog040;	break;
            case 41:	 key = R.string.dog041;	break;
            case 42:	 key = R.string.dog042;	break;
            case 43:	 key = R.string.dog043;	break;
            case 44:	 key = R.string.dog044;	break;
            case 45:	 key = R.string.dog045;	break;
            case 46:	 key = R.string.dog046;	break;
            case 47:	 key = R.string.dog047;	break;
            case 48:	 key = R.string.dog048;	break;
            case 49:	 key = R.string.dog049;	break;
            case 50:	 key = R.string.dog050;	break;
            case 51:	 key = R.string.dog051;	break;
            case 52:	 key = R.string.dog052;	break;
            case 53:	 key = R.string.dog053;	break;
            case 54:	 key = R.string.dog054;	break;
            case 55:	 key = R.string.dog055;	break;
            case 56:	 key = R.string.dog056;	break;
            case 57:	 key = R.string.dog057;	break;
            case 58:	 key = R.string.dog058;	break;
            case 59:	 key = R.string.dog059;	break;
            case 60:	 key = R.string.dog060;	break;
            case 61:	 key = R.string.dog061;	break;
            case 62:	 key = R.string.dog062;	break;
            case 63:	 key = R.string.dog063;	break;
            case 64:	 key = R.string.dog064;	break;
            case 65:	 key = R.string.dog065;	break;
            case 66:	 key = R.string.dog066;	break;
            case 67:	 key = R.string.dog067;	break;
            case 68:	 key = R.string.dog068;	break;
            case 69:	 key = R.string.dog069;	break;
            case 70:	 key = R.string.dog070;	break;
            case 71:	 key = R.string.dog071;	break;
            case 72:	 key = R.string.dog072;	break;
            case 73:	 key = R.string.dog073;	break;
            case 74:	 key = R.string.dog074;	break;
            case 75:	 key = R.string.dog075;	break;
            case 76:	 key = R.string.dog076;	break;
            case 77:	 key = R.string.dog077;	break;
            case 78:	 key = R.string.dog078;	break;
            case 79:	 key = R.string.dog079;	break;
            case 80:	 key = R.string.dog080;	break;
            case 81:	 key = R.string.dog081;	break;
            case 82:	 key = R.string.dog082;	break;
            case 83:	 key = R.string.dog083;	break;
            case 84:	 key = R.string.dog084;	break;
            case 85:	 key = R.string.dog085;	break;
            case 86:	 key = R.string.dog086;	break;
            case 87:	 key = R.string.dog087;	break;
            case 88:	 key = R.string.dog088;	break;
            case 89:	 key = R.string.dog089;	break;
            case 90:	 key = R.string.dog090;	break;
            case 91:	 key = R.string.dog091;	break;
            case 92:	 key = R.string.dog092;	break;
            case 93:	 key = R.string.dog093;	break;
            case 94:	 key = R.string.dog094;	break;
            case 95:	 key = R.string.dog095;	break;
            case 96:	 key = R.string.dog096;	break;
            case 97:	 key = R.string.dog097;	break;
            case 98:	 key = R.string.dog098;	break;
            case 99:	 key = R.string.dog099;	break;
            case 100:	 key = R.string.dog100;	break;
            case 101:	 key = R.string.dog101;	break;
            case 102:	 key = R.string.dog102;	break;
            case 103:	 key = R.string.dog103;	break;
            case 104:	 key = R.string.dog104;	break;
            case 105:	 key = R.string.dog105;	break;
            case 106:	 key = R.string.dog106;	break;
            case 107:	 key = R.string.dog107;	break;
            case 108:	 key = R.string.dog108;	break;
            case 109:	 key = R.string.dog109;	break;
            case 110:	 key = R.string.dog110;	break;
            case 111:	 key = R.string.dog111;	break;
            case 112:	 key = R.string.dog112;	break;
            case 113:	 key = R.string.dog113;	break;
            case 114:	 key = R.string.dog114;	break;
            case 115:	 key = R.string.dog115;	break;
            case 116:	 key = R.string.dog116;	break;
            case 117:	 key = R.string.dog117;	break;
            case 118:	 key = R.string.dog118;	break;
            case 119:	 key = R.string.dog119;	break;
            case 120:	 key = R.string.dog120;	break;
            case 121:	 key = R.string.dog121;	break;
            case 122:	 key = R.string.dog122;	break;
            case 123:	 key = R.string.dog123;	break;
            case 124:	 key = R.string.dog124;	break;
            case 125:	 key = R.string.dog125;	break;
            case 126:	 key = R.string.dog126;	break;
            case 127:	 key = R.string.dog127;	break;
            case 128:	 key = R.string.dog128;	break;
            case 129:	 key = R.string.dog129;	break;
            case 130:	 key = R.string.dog130;	break;
            case 131:	 key = R.string.dog131;	break;
            case 132:	 key = R.string.dog132;	break;
            case 133:	 key = R.string.dog133;	break;
            case 134:	 key = R.string.dog134;	break;
            case 135:	 key = R.string.dog135;	break;
            case 136:	 key = R.string.dog136;	break;
            case 137:	 key = R.string.dog137;	break;
            case 138:	 key = R.string.dog138;	break;
            case 139:	 key = R.string.dog139;	break;
            case 140:	 key = R.string.dog140;	break;
            case 141:	 key = R.string.dog141;	break;
            case 142:	 key = R.string.dog142;	break;
            case 143:	 key = R.string.dog143;	break;
            case 144:	 key = R.string.dog144;	break;
            case 145:	 key = R.string.dog145;	break;
            case 146:	 key = R.string.dog146;	break;
            case 147:	 key = R.string.dog147;	break;
            case 148:	 key = R.string.dog148;	break;
            case 149:	 key = R.string.dog149;	break;
            case 150:	 key = R.string.dog150;	break;
            case 151:	 key = R.string.dog151;	break;
            case 152:	 key = R.string.dog152;	break;
            case 153:	 key = R.string.dog153;	break;
            case 154:	 key = R.string.dog154;	break;

            default:     key = R.string.dog154; break;
        }

        return key;
    }

    private static int getCatKey(int index) {
        int key;

        switch (index) {
            case 1:
                key = R.string.cat001;
                break;
            case 2:
                key = R.string.cat002;
                break;
            case 3:
                key = R.string.cat003;
                break;
            case 4:
                key = R.string.cat004;
                break;
            case 5:
                key = R.string.cat005;
                break;
            case 6:
                key = R.string.cat006;
                break;
            case 7:
                key = R.string.cat007;
                break;
            case 8:
                key = R.string.cat008;
                break;
            case 9:
                key = R.string.cat009;
                break;
            case 10:
                key = R.string.cat010;
                break;
            case 11:
                key = R.string.cat011;
                break;
            case 12:
                key = R.string.cat012;
                break;
            case 13:
                key = R.string.cat013;
                break;
            case 14:
                key = R.string.cat014;
                break;
            case 15:
                key = R.string.cat015;
                break;
            case 16:
                key = R.string.cat016;
                break;
            case 17:
                key = R.string.cat017;
                break;
            case 18:
                key = R.string.cat018;
                break;
            case 19:
                key = R.string.cat019;
                break;
            case 20:
                key = R.string.cat020;
                break;
            case 21:
                key = R.string.cat021;
                break;
            case 22:
                key = R.string.cat022;
                break;
            case 23:
                key = R.string.cat023;
                break;
            case 24:
                key = R.string.cat024;
                break;
            case 25:
                key = R.string.cat025;
                break;
            case 26:
                key = R.string.cat026;
                break;
            case 27:
                key = R.string.cat027;
                break;
            case 28:
                key = R.string.cat028;
                break;
            case 29:
                key = R.string.cat029;
                break;
            case 30:
                key = R.string.cat030;
                break;
            case 31:
                key = R.string.cat031;
                break;
            case 32:
                key = R.string.cat032;
                break;
            case 33:
                key = R.string.cat033;
                break;
            case 34:
                key = R.string.cat034;
                break;
            case 35:
                key = R.string.cat035;
                break;
            case 36:
                key = R.string.cat036;
                break;
            case 37:
                key = R.string.cat037;
                break;
            case 38:
                key = R.string.cat038;
                break;
            case 39:
                key = R.string.cat039;
                break;
            case 40:
                key = R.string.cat040;
                break;
            case 41:
                key = R.string.cat041;
                break;
            case 42:
                key = R.string.cat042;
                break;
            case 43:
                key = R.string.cat043;
                break;
            case 44:
                key = R.string.cat044;
                break;
            case 45:
                key = R.string.cat045;
                break;
            case 46:
                key = R.string.cat046;
                break;
            case 47:
                key = R.string.cat047;
                break;
            case 48:
                key = R.string.cat048;
                break;
            case 49:
                key = R.string.cat049;
                break;
            case 50:
                key = R.string.cat050;
                break;
            case 51:
                key = R.string.cat051;
                break;
            case 52:
                key = R.string.cat052;
                break;
            case 53:
                key = R.string.cat053;
                break;
            case 54:
                key = R.string.cat054;
                break;
            case 55:
                key = R.string.cat055;
                break;
            case 56:
                key = R.string.cat056;
                break;
            case 57:
                key = R.string.cat057;
                break;
            case 58:
                key = R.string.cat058;
                break;
            case 59:
                key = R.string.cat059;
                break;
            case 60:
                key = R.string.cat060;
                break;
            case 61:
                key = R.string.cat061;
                break;
            case 62:
                key = R.string.cat062;
                break;
            case 63:
                key = R.string.cat063;
                break;
            case 64:
                key = R.string.cat064;
                break;
            case 65:
                key = R.string.cat065;
                break;
            case 66:
                key = R.string.cat066;
                break;
            case 67:
                key = R.string.cat067;
                break;
            case 68:
                key = R.string.cat068;
                break;
            case 69:
                key = R.string.cat069;
                break;
            case 70:
                key = R.string.cat070;
                break;
            case 71:
                key = R.string.cat071;
                break;
            case 72:
                key = R.string.cat072;
                break;
            case 73:
                key = R.string.cat073;
                break;
            case 74:
                key = R.string.cat074;
                break;
            case 75:
                key = R.string.cat075;
                break;
            case 76:
                key = R.string.cat076;
                break;
            case 77:
                key = R.string.cat077;
                break;
            case 78:
                key = R.string.cat078;
                break;
            case 79:
                key = R.string.cat079;
                break;
            case 80:
                key = R.string.cat080;
                break;
            case 81:
                key = R.string.cat081;
                break;
            case 82:
                key = R.string.cat082;
                break;
            case 83:
                key = R.string.cat083;
                break;
            case 84:
                key = R.string.cat084;
                break;
            case 85:
                key = R.string.cat085;
                break;
            case 86:
                key = R.string.cat086;
                break;
            case 87:
                key = R.string.cat087;
                break;
            case 88:
                key = R.string.cat088;
                break;
            case 89:
                key = R.string.cat089;
                break;
            case 90:
                key = R.string.cat090;
                break;
            case 91:
                key = R.string.cat091;
                break;
            case 92:
                key = R.string.cat092;
                break;
            case 93:
                key = R.string.cat093;
                break;
            case 94:
                key = R.string.cat094;
                break;
            case 95:
                key = R.string.cat095;
                break;
            case 96:
                key = R.string.cat096;
                break;
            case 97:
                key = R.string.cat097;
                break;
            case 98:
                key = R.string.cat098;
                break;
            case 99:
                key = R.string.cat099;
                break;
            case 100:
                key = R.string.cat100;
                break;
            case 101:
                key = R.string.cat101;
                break;
            case 102:
                key = R.string.cat102;
                break;
            case 103:
                key = R.string.cat103;
                break;
            case 104:
                key = R.string.cat104;
                break;
            case 105:
                key = R.string.cat105;
                break;
            case 106:
                key = R.string.cat106;
                break;
            case 107:
                key = R.string.cat107;
                break;
            case 108:
                key = R.string.cat108;
                break;
            default:
                key = R.string.cat108;
                break;
        }

        return key;
    }
}
