/*
 * Copyright (c) 2009-2015 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.shader;

import java.util.List;

/**
 * The new define list.
 * 
 * @author Kirill Vainer
 */
public final class DefineList implements Cloneable {

    public static final int MAX_DEFINES = 64;
    
    public static final int SAVABLE_VERSION = 1;
    
    private long hash;
    private final int[] vals;

    public DefineList(int numValues) {
        if (numValues < 0 || numValues > MAX_DEFINES) {
            throw new IllegalArgumentException("numValues must be between 0 and 64");
        }
        vals = new int[numValues];
    }
    
    private DefineList(DefineList original) {
        this.hash = original.hash;
        this.vals = new int[original.vals.length];
        System.arraycopy(original.vals, 0, vals, 0, vals.length);
    }

    public void set(int id, int val) {
        assert 0 <= id && id < 64;
        if (val != 0) {
            hash |=  (1L << id);
        } else {
            hash &= ~(1L << id);
        }
        vals[id] = val;
    }
    
    public void set(int id, float val) {
        set(id, Float.floatToIntBits(val));
    }
    
    public void set(int id, boolean val) {
        set(id, val ? 1 : 0);
    }

    @Override
    public int hashCode() {
        return (int)((hash >> 32) ^ hash);
    }

    @Override
    public boolean equals(Object other) {
         DefineList o = (DefineList) other;
         if (hash == o.hash) {
             for (int i = 0; i < vals.length; i++) {
                  if (vals[i] != o.vals[i]) return false;
             }
             return true;
         }
         return false;
    }

    public DefineList deepClone() {
         return new DefineList(this);
    }
    
    public void generateSource(StringBuilder sb, List<String> defineNames, List<VarType> defineTypes) {
        for (int i = 0; i < vals.length; i++) {
            if (vals[i] != 0) {
                String defineName = defineNames.get(i);
                
                sb.append("#define ");
                sb.append(defineName);
                sb.append(" ");
                
                if (defineTypes != null && defineTypes.get(i) == VarType.Float) {
                    float val = Float.intBitsToFloat(vals[i]);
                    if (!Float.isFinite(val)) {
                        throw new IllegalArgumentException(
                                "GLSL does not support NaN "
                                + "or Infinite float literals");
                    }
                    sb.append(val);
                } else {
                    sb.append(vals[i]);
                }
                
                sb.append("\n");
            }
        }
        System.out.println(sb.toString());
    }
}