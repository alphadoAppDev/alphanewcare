
package kr.co.alphacare.pets;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.ToLongFunction;

import kr.co.alphacare.R;

public final class PetKind {

    final String TAG = "PetKind";

    public static String GetPetKind(Context context, int type, int kind) {
        int length = getCount(type);
        String[] szValue;
        String result;

        result = "";

        for(int i = 1; i < length; i++)
        {

            szValue = context.getResources().getString(getKey(type, i)).split(",");
            if (kind == Integer.parseInt(szValue[0])) {
                result = szValue[1];
                break;
            }

            Log.e("PetKind", "dog" + i + ": kind: " +szValue[0] + ", name: " + szValue[1]);
        }

        return result;
    }

//    class MapComparator implements Comparator<Map<String, Object>> {
//        private final String name;
//
//        public MapComparator(String name){
//            this.name = name;
//        }
//
//        @Override
//        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//            int result = o1.get(name).compareTo(o2.get(name));
//            return 0;
//        }
//
//    }

    public static List<Map<String, Object>> GetPetKindList(Context context, int type) {
        List<Map<String, Object>> dialogItemList;
        String[] szValue;

        int length = getCount(type);


        dialogItemList = new ArrayList<>();

        for(int i = 1; i < length; i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            szValue = context.getResources().getString(getKey(type, i)).split(",");

            itemMap.put("kind", szValue[0]);
            itemMap.put("name", szValue[1]);

            dialogItemList.add(itemMap);

            Log.e("PetKind", "dog" + i + ": kind: " +szValue[0] + ", name: " + szValue[1]);
        }


        /**
         * 210113 정렬 기능 추가
         * */
        Collections.sort(dialogItemList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {

                String firstValue = (String) o1.get("name");
                String secondValue = (String) o2.get("name");


                if (secondValue.compareTo(firstValue) > 0) {
                    return -1;
                }else if (secondValue.compareTo(firstValue) < 0) {
                    return 1;
                }else {
                    return 0;
                }

            }
        });


        return dialogItemList;
    }

    private static int getCount(int type) {
        if (type == 1) return 348;
        else return 109;
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
            case 155:    key = R.string.dog155;	break;
            case 156:    key = R.string.dog156;	break;
            case 157:    key = R.string.dog157;	break;
            case 158:    key = R.string.dog158;	break;
            case 159:    key = R.string.dog159;	break;
            case 160:    key = R.string.dog160;	break;
            case 161:    key = R.string.dog161;	break;
            case 162:    key = R.string.dog162;	break;
            case 163:    key = R.string.dog163;	break;
            case 164:    key = R.string.dog164;	break;
            case 165:    key = R.string.dog165;	break;
            case 166:    key = R.string.dog166;	break;
            case 167:    key = R.string.dog167;	break;
            case 168:    key = R.string.dog168;	break;
            case 169:    key = R.string.dog169;	break;
            case 170:    key = R.string.dog170;	break;
            case 171:    key = R.string.dog171;	break;
            case 172:    key = R.string.dog172;	break;
            case 173:    key = R.string.dog173;	break;
            case 174:    key = R.string.dog174;	break;
            case 175:    key = R.string.dog175;	break;
            case 176:    key = R.string.dog176;	break;
            case 177:    key = R.string.dog177;	break;
            case 178:    key = R.string.dog178;	break;
            case 179:    key = R.string.dog179;	break;
            case 180:    key = R.string.dog180;	break;
            case 181:    key = R.string.dog181;	break;
            case 182:    key = R.string.dog182;	break;
            case 183:    key = R.string.dog183;	break;
            case 184:    key = R.string.dog184;	break;
            case 185:    key = R.string.dog185;	break;
            case 186:    key = R.string.dog186;	break;
            case 187:    key = R.string.dog187;	break;
            case 188:    key = R.string.dog188;	break;
            case 189:    key = R.string.dog189;	break;
            case 190:    key = R.string.dog190;	break;
            case 191:    key = R.string.dog191;	break;
            case 192:    key = R.string.dog192;	break;
            case 193:    key = R.string.dog193;	break;
            case 194:    key = R.string.dog194;	break;
            case 195:    key = R.string.dog195;	break;
            case 196:    key = R.string.dog196;	break;
            case 197:    key = R.string.dog197;	break;
            case 198:    key = R.string.dog198;	break;
            case 199:    key = R.string.dog199;	break;
            case 200:    key = R.string.dog200;	break;
            case 201:    key = R.string.dog201;	break;
            case 202:    key = R.string.dog202;	break;
            case 203:    key = R.string.dog203;	break;
            case 204:    key = R.string.dog204;	break;
            case 205:    key = R.string.dog205;	break;
            case 206:    key = R.string.dog206;	break;
            case 207:    key = R.string.dog207;	break;
            case 208:    key = R.string.dog208;	break;
            case 209:    key = R.string.dog209;	break;
            case 210:    key = R.string.dog210;	break;
            case 211:    key = R.string.dog211;	break;
            case 212:    key = R.string.dog212;	break;
            case 213:    key = R.string.dog213;	break;
            case 214:    key = R.string.dog214;	break;
            case 215:    key = R.string.dog215;	break;
            case 216:    key = R.string.dog216;	break;
            case 217:    key = R.string.dog217;	break;
            case 218:    key = R.string.dog218;	break;
            case 219:    key = R.string.dog219;	break;
            case 220:    key = R.string.dog220;	break;
            case 221:    key = R.string.dog221;	break;
            case 222:    key = R.string.dog222;	break;
            case 223:    key = R.string.dog223;	break;
            case 224:    key = R.string.dog224;	break;
            case 225:    key = R.string.dog225;	break;
            case 226:    key = R.string.dog226;	break;
            case 227:    key = R.string.dog227;	break;
            case 228:    key = R.string.dog228;	break;
            case 229:    key = R.string.dog229;	break;
            case 230:    key = R.string.dog230;	break;
            case 231:    key = R.string.dog231;	break;
            case 232:    key = R.string.dog232;	break;
            case 233:    key = R.string.dog233;	break;
            case 234:    key = R.string.dog234;	break;
            case 235:    key = R.string.dog235;	break;
            case 236:    key = R.string.dog236;	break;
            case 237:    key = R.string.dog237;	break;
            case 238:    key = R.string.dog238;	break;
            case 239:    key = R.string.dog239;	break;
            case 240:    key = R.string.dog240;	break;
            case 241:    key = R.string.dog241;	break;
            case 242:    key = R.string.dog242;	break;
            case 243:    key = R.string.dog243;	break;
            case 244:    key = R.string.dog244;	break;
            case 245:    key = R.string.dog245;	break;
            case 246:    key = R.string.dog246;	break;
            case 247:    key = R.string.dog247;	break;
            case 248:    key = R.string.dog248;	break;
            case 249:    key = R.string.dog249;	break;
            case 250:    key = R.string.dog250;	break;
            case 251:    key = R.string.dog251;	break;
            case 252:    key = R.string.dog252;	break;
            case 253:    key = R.string.dog253;	break;
            case 254:    key = R.string.dog254;	break;
            case 255:    key = R.string.dog255;	break;
            case 256:    key = R.string.dog256;	break;
            case 257:    key = R.string.dog257;	break;
            case 258:    key = R.string.dog258;	break;
            case 259:    key = R.string.dog259;	break;
            case 260:    key = R.string.dog260;	break;
            case 261:    key = R.string.dog261;	break;
            case 262:    key = R.string.dog262;	break;
            case 263:    key = R.string.dog263;	break;
            case 264:    key = R.string.dog264;	break;
            case 265:    key = R.string.dog265;	break;
            case 266:    key = R.string.dog266;	break;
            case 267:    key = R.string.dog267;	break;
            case 268:    key = R.string.dog268;	break;
            case 269:    key = R.string.dog269;	break;
            case 270:    key = R.string.dog270;	break;
            case 271:    key = R.string.dog271;	break;
            case 272:    key = R.string.dog272;	break;
            case 273:    key = R.string.dog273;	break;
            case 274:    key = R.string.dog274;	break;
            case 275:    key = R.string.dog275;	break;
            case 276:    key = R.string.dog276;	break;
            case 277:    key = R.string.dog277;	break;
            case 278:    key = R.string.dog278;	break;
            case 279:    key = R.string.dog279;	break;
            case 280:    key = R.string.dog280;	break;
            case 281:    key = R.string.dog281;	break;
            case 282:    key = R.string.dog282;	break;
            case 283:    key = R.string.dog283;	break;
            case 284:    key = R.string.dog284;	break;
            case 285:    key = R.string.dog285;	break;
            case 286:    key = R.string.dog286;	break;
            case 287:    key = R.string.dog287;	break;
            case 288:    key = R.string.dog288;	break;
            case 289:    key = R.string.dog289;	break;
            case 290:    key = R.string.dog290;	break;
            case 291:    key = R.string.dog291;	break;
            case 292:    key = R.string.dog292;	break;
            case 293:    key = R.string.dog293;	break;
            case 294:    key = R.string.dog294;	break;
            case 295:    key = R.string.dog295;	break;
            case 296:    key = R.string.dog296;	break;
            case 297:    key = R.string.dog297;	break;
            case 298:    key = R.string.dog298;	break;
            case 299:    key = R.string.dog299;	break;
            case 300:    key = R.string.dog300;	break;
            case 301:    key = R.string.dog301;	break;
            case 302:    key = R.string.dog302;	break;
            case 303:    key = R.string.dog303;	break;
            case 304:    key = R.string.dog304;	break;
            case 305:    key = R.string.dog305;	break;
            case 306:    key = R.string.dog306;	break;
            case 307:    key = R.string.dog307;	break;
            case 308:    key = R.string.dog308;	break;
            case 309:    key = R.string.dog309;	break;
            case 310:    key = R.string.dog310;	break;
            case 311:    key = R.string.dog311;	break;
            case 312:    key = R.string.dog312;	break;
            case 313:    key = R.string.dog313;	break;
            case 314:    key = R.string.dog314;	break;
            case 315:    key = R.string.dog315;	break;
            case 316:    key = R.string.dog316;	break;
            case 317:    key = R.string.dog317;	break;
            case 318:    key = R.string.dog318;	break;
            case 319:    key = R.string.dog319;	break;
            case 320:    key = R.string.dog320;	break;
            case 321:    key = R.string.dog321;	break;
            case 322:    key = R.string.dog322;	break;
            case 323:    key = R.string.dog323;	break;
            case 324:    key = R.string.dog324;	break;
            case 325:    key = R.string.dog325;	break;
            case 326:    key = R.string.dog326;	break;
            case 327:    key = R.string.dog327;	break;
            case 328:    key = R.string.dog328;	break;
            case 329:    key = R.string.dog329;	break;
            case 330:    key = R.string.dog330;	break;
            case 331:    key = R.string.dog331;	break;
            case 332:    key = R.string.dog332;	break;
            case 333:    key = R.string.dog333;	break;
            case 334:    key = R.string.dog334;	break;
            case 335:    key = R.string.dog335;	break;
            case 336:    key = R.string.dog336;	break;
            case 337:    key = R.string.dog337;	break;
            case 338:    key = R.string.dog338;	break;
            case 339:    key = R.string.dog339;	break;
            case 340:    key = R.string.dog340;	break;
            case 341:    key = R.string.dog341;	break;
            case 342:    key = R.string.dog342;	break;
            case 343:    key = R.string.dog343;	break;
            case 344:    key = R.string.dog344;	break;
            case 345:    key = R.string.dog345;	break;
            case 346:    key = R.string.dog346;	break;
            case 347:    key = R.string.dog347;	break;
            default:     key = R.string.dog347; break;
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
