package bugs.debug;

import org.junit.Test;

import bugs.VerifyCompliancePeople;

@SuppressWarnings("javadoc")
public class VerifyCompliancePeopleBroken extends VerifyCompliancePeople {

    @Test
    public void shouldPassgetSuperClassesmad_cowtrue() {
        // expected white_van_man, tiger, cat, van, pet, dog, cow, dog_owner,
        // old_lady, duck, animal_lover, white_thing
        // actual__ , , , , , , , , , , , , , , , , , , , ,
        equal(reasoner.getSuperClasses(mad_cow, true), truck, lorry,
                white_van_man, car, tiger, cat, van, magazine, pet, dog,
                bus_driver, haulage_truck_driver, cow, tree, dog_owner,
                giraffe, red_top, brain, bus, quality_broadsheet, lorry_driver,
                bicycle, sheep, old_lady, kid, bus_company, duck,
                haulage_company, animal_lover, white_thing, leaf, bone, grass);
    }
}
