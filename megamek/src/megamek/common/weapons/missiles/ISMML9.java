/*
 * Copyright (c) 2005 - Ben Mazur (bmazur@sev.org)
 * Copyright (c) 2022 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.common.weapons.missiles;

import megamek.common.alphaStrike.BattleForceElement;
import megamek.common.Mounted;

import static megamek.common.MountedHelper.isArtemisIV;
import static megamek.common.MountedHelper.isArtemisProto;

/**
 * @author Sebastian Brocks
 */
public class ISMML9 extends MMLWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -6856580158397507743L;

    /**
     *
     */
    public ISMML9() {
        super();
        name = "MML 9";
        setInternalName("ISMML9");
        addLookupName("IS MML-9");
        heat = 5;
        rackSize = 9;
        tonnage = 6;
        criticals = 5;
        bv = 86;
        cost = 125000;
        shortAV = 5;
        medAV = 5;
        longAV = 5;
        maxRange = RANGE_LONG;
        rulesRefs = "229, TM";
        techAdvancement.setTechBase(TECH_BASE_IS)
        	.setIntroLevel(false)
        	.setUnofficial(false)
            .setTechRating(RATING_D)
            .setAvailability(RATING_X, RATING_X, RATING_E, RATING_D)
            .setISAdvancement(3067, 3068, 3072, DATE_NONE, DATE_NONE)
            .setISApproximate(true, false, false,false, false)
            .setPrototypeFactions(F_MERC)
            .setProductionFactions(F_WB);
    }

    @Override
    public double getBattleForceDamage(int range, Mounted fcs) {
        if (range == BattleForceElement.SHORT_RANGE) {
            return (isArtemisIV(fcs) || isArtemisProto(fcs)) ? 1.4 : 1;
        } else if (range == BattleForceElement.MEDIUM_RANGE) {
            return (isArtemisIV(fcs) || isArtemisProto(fcs)) ? 1.05 : 0.75;
        } else if (range == BattleForceElement.LONG_RANGE) {
            return (isArtemisIV(fcs) || isArtemisProto(fcs)) ? 0.7 : 0.5;
        } else {
            return 0;
        }
    }
}
