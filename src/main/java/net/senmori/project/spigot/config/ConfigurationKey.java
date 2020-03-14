/*
 * Copyright (c) 2018, Senmori. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * The name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package net.senmori.project.spigot.config;

public interface ConfigurationKey {

    final class Name {

        /* *********************************
         *          URLS
         * *********************************
         */

        public static final String SPIGOT_VERSION_LINK = "spigot_versions_link";

        public static final String GIT_INSTALLER_LINK = "git_installer_link";

        public static final String MAVEN_INSTALLER_LINK = "maven_installer_link";

        public static final String STASH_REPO_LINK = "stash_repo_link";

        public static final String S3_DOWNLOAD_LINK = "s3_download_link";

        public static final String MC_JAR_DOWNLOAD_LINK = "mc_jar_download_link";

        /* *********************************
         *          VERSIONS
         * *********************************
         */

        public static final String PORTABLE_GIT_VERSION = "versions.portable_git";

        public static final String DEFAULT_SPIGOT_VERSION = "versions.spigot";

        public static final String MAVEN_VERSION = "versions.maven";

        public static final String PORTABLE_GIT_DIR = "portable_git_dir";
    }
}
