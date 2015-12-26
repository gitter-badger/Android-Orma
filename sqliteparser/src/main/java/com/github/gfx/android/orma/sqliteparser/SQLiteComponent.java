/*
 * Copyright (c) 2015 FUJI Goro (gfx).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gfx.android.orma.sqliteparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * Base class of SQLite components
 */
public class SQLiteComponent {

    protected final List<CharSequence> tokens = new ArrayList<>();

    public List<CharSequence> getTokens() {
        return tokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SQLiteComponent)) {
            return false;
        }

        SQLiteComponent that = (SQLiteComponent) o;
        return tokens.equals(that.tokens);

    }

    @Override
    public int hashCode() {
        return tokens.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (CharSequence token : tokens) {
            if (sb.length() != 0) {
                sb.append(' ');
            }
            sb.append(token);
        }

        return sb.toString();
    }

    public static class CaseInsensitiveToken implements CharSequence {

        @Nonnull
        final String token;

        public CaseInsensitiveToken(@Nonnull String token) {
            this.token = token;
        }

        @Override
        public int length() {
            return token.length();
        }

        @Override
        public char charAt(int index) {
            return token.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return token.subSequence(start, end);
        }

        @Nonnull
        @Override
        public String toString() {
            return token;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CaseInsensitiveToken)) {
                return false;
            }

            CaseInsensitiveToken that = (CaseInsensitiveToken) o;
            return token.equalsIgnoreCase(that.token);

        }

        @Override
        public int hashCode() {
            return token.toLowerCase(Locale.US).hashCode();
        }
    }

    public static class Keyword extends CaseInsensitiveToken {

        public Keyword(@Nonnull String token) {
            super(token);
        }
    }

    public static class Name extends CaseInsensitiveToken {

        public Name(@Nonnull String token) {
            super(ensureDoubleQuoted(token));
        }

        @Nonnull
        public String getUnquotedToken() {
            return dequote(token);
        }

        @Nonnull
        @Override
        public String toString() {
            return token;
        }

        static String ensureDoubleQuoted(String name) {
            return '"' + dequote(name) + '"';
        }

        static String dequote(String maybeQuoted) {
            if (maybeQuoted.charAt(0) == '"' || maybeQuoted.charAt(0) == '`') {
                return maybeQuoted.substring(1, maybeQuoted.length() - 1);
            } else {
                return maybeQuoted;
            }
        }
    }
}
