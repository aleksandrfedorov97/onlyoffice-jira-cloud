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

export const downloadUrl = (url: string) => {
  const isIE11 =
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    !!(window as any).MSInputMethodContext && !!(document as any).documentMode;
  const isSafari = /^((?!chrome|android).)*safari/i.test(
    (navigator as Navigator).userAgent,
  );

  const iframeName = "media-download-iframe-onlyoffice-jira-plugin";
  const link = document.createElement("a");
  let iframe = document.getElementById(iframeName) as HTMLIFrameElement;

  if (!iframe) {
    iframe = document.createElement("iframe");
    iframe.style.display = "none";
    iframe.id = iframeName;
    iframe.name = iframeName;
    document.body.appendChild(iframe);
  }

  link.href = url;
  link.target = isIE11 || isSafari ? "_blank" : iframeName;

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};
