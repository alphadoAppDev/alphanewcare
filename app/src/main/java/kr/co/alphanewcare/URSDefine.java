package kr.co.alphanewcare;

public class URSDefine
{
    // 자동촬영 - 연속 성공 프레임 갯수 설정
    public static final float AUTO_SHOT_SUCCESS_COUNT = 3;

    //public static final int[] litmus10_comparisionRange = {3, 7, 13, 18, 24, 26, 31, 37, 45, 50};
    public static final int[] litmus10_comparisionRange = {4, 6, 11, 17, 24, 29, 36, 42, 46, 52};
    public static final int[] slim_diet_stix_comparisionRange = {3};
    public static final int[] self_stik_fr_comparisionRange = {5};
    public static final int[] nitric_oxide_comparisionRange = {3};

    public enum PaperMode
    {
        litmus10(1),
        slim_diet_stix(2),
        self_stik_fr(3),
        nitric_oxide(4);

        final private int paperMode;

        PaperMode(int paperMode)
        {
            this.paperMode = paperMode;
        }

        public int getValue()
        {
            return paperMode;
        }
    }
}
