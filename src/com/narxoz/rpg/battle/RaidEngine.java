package com.narxoz.rpg.battle;

import com.narxoz.rpg.bridge.Skill;
import com.narxoz.rpg.composite.CombatNode;

import java.util.Random;

public class RaidEngine {
    private Random random = new Random(1L);

    public RaidEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public RaidResult runRaid(CombatNode teamA, CombatNode teamB, Skill teamASkill, Skill teamBSkill) {
        if (teamA == null || teamB == null) {
            throw new IllegalArgumentException("Teams must not be null.");
        }
        if (teamASkill == null || teamBSkill == null) {
            throw new IllegalArgumentException("Skills must not be null.");
        }
        if (!teamA.isAlive() || !teamB.isAlive()) {
            throw new IllegalArgumentException("Both teams must be alive before the raid starts.");
        }

        RaidResult result = new RaidResult();
        int rounds = 0;
        final int maxRounds = 100;

        result.addLine("Raid started: " + teamA.getName() + " vs " + teamB.getName());

        while (teamA.isAlive() && teamB.isAlive() && rounds < maxRounds) {
            rounds++;
            result.addLine("=== Round " + rounds + " ===");

            if (teamA.isAlive()) {
                boolean critA = random.nextInt(100) < 10;
                int beforeHpB = teamB.getHealth();

                teamASkill.cast(teamB);

                if (critA && teamB.isAlive()) {
                    int bonus = Math.max(1, teamA.getAttackPower() / 2);
                    teamB.takeDamage(bonus);
                    result.addLine(teamA.getName() + " lands a CRITICAL bonus hit for " + bonus + " damage!");
                }

                int dealtA = Math.max(0, beforeHpB - teamB.getHealth());
                result.addLine(teamA.getName() + " uses " + teamASkill.getSkillName() + " (" +
                        teamASkill.getEffectName() + ") on " + teamB.getName() +
                        " for " + dealtA + " damage.");
            }


            if (teamB.isAlive()) {
                boolean critB = random.nextInt(100) < 10;
                int beforeHpA = teamA.getHealth();

                teamBSkill.cast(teamA);

                if (critB && teamA.isAlive()) {
                    int bonus = Math.max(1, teamB.getAttackPower() / 2);
                    teamA.takeDamage(bonus);
                    result.addLine(teamB.getName() + " lands a CRITICAL bonus hit for " + bonus + " damage!");
                }

                int dealtB = Math.max(0, beforeHpA - teamA.getHealth());
                result.addLine(teamB.getName() + " uses " + teamBSkill.getSkillName() + " (" +
                        teamBSkill.getEffectName() + ") on " + teamA.getName() +
                        " for " + dealtB + " damage.");
            }

            result.addLine(teamA.getName() + " HP: " + teamA.getHealth());
            result.addLine(teamB.getName() + " HP: " + teamB.getHealth());
        }

        result.setRounds(rounds);

        if (teamA.isAlive() && !teamB.isAlive()) {
            result.setWinner(teamA.getName());
        } else if (teamB.isAlive() && !teamA.isAlive()) {
            result.setWinner(teamB.getName());
        } else if (teamA.isAlive() && teamB.isAlive()) {
            result.setWinner("Draw");
            result.addLine("Max rounds reached.");
        } else {
            result.setWinner("Draw");
        }

        result.addLine("Raid finished. Winner: " + result.getWinner());
        return result;
    }
}
