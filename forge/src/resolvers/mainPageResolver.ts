/**
 *
 * (c) Copyright Ascensio System SIA 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import Resolver, { Request } from "@forge/resolver";

import {
  getAttachmentContentUrl,
  getRemoteFormats,
  postRemoteAppAuthorization,
  postRemoteCreateAttachment,
} from "../../src/client";
import { Format } from "../types/types";

const FORMATS_CACHE_TTL = 1000 * 60 * 60 * 24;
let formatsLastFetched = 0;
let formats: Format[] = [];

const mainPageResolver = new Resolver();

mainPageResolver.define("authorizeRemoteApp", async (request: Request) => {
  const { issueId, attachmentId } = request.payload;

  return await postRemoteAppAuthorization(issueId, attachmentId);
});

mainPageResolver.define("createAttachment", async (request: Request) => {
  const { issueId, title, type, locale } = request.payload;

  return await postRemoteCreateAttachment(issueId, title, type, locale);
});

mainPageResolver.define("getAttachmentContentUrl", async (request: Request) => {
  const { attachmentId } = request.payload;

  return await getAttachmentContentUrl(attachmentId);
});

mainPageResolver.define("getFormats", async () => {
  const now = Date.now();

  if (formats.length === 0 || now - formatsLastFetched > FORMATS_CACHE_TTL) {
    formats = await getRemoteFormats();
    formatsLastFetched = now;
  }

  return formats;
});

export default mainPageResolver.getDefinitions();
